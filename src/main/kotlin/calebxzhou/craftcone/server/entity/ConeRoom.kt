package calebxzhou.craftcone.server.entity

import calebxzhou.craftcone.net.ConeNetSender
import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.coneErrD
import calebxzhou.craftcone.net.coneInfoT
import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.net.protocol.game.BlockDataC2CPacket
import calebxzhou.craftcone.net.protocol.game.PlayerJoinedRoomS2CPacket
import calebxzhou.craftcone.net.protocol.game.PlayerLeftRoomS2CPacket
import calebxzhou.craftcone.net.protocol.general.OkDataS2CPacket
import calebxzhou.craftcone.server.DB
import calebxzhou.craftcone.server.logger
import org.bson.codecs.pojo.annotations.BsonId
import kotlin.random.Random

/**
 * Created  on 2023-08-03,13:20.
 */
data class ConeRoom(
    @BsonId val id: Int,
    val name: String,
    val owner: ConePlayer,
    val mcVer: String,
    val isCreative: Boolean,
    val blockStateAmount: Int,
    val seed: Long,
    val createTime: Long,
    val blockData: MutableList<ConeBlockData> = arrayListOf()
) : Packet, BufferWritable {

    //写入到ByteBuf
    override fun write(buf: FriendlyByteBuf) {
        buf.writeVarInt(id)
        buf.writeUtf(name)
        buf.writeUtf(mcVer)
        buf.writeBoolean(isCreative)
        buf.writeVarInt(blockStateAmount)
        buf.writeLong(seed)
        buf.writeLong(createTime)
    }

    //房间在线玩家list
    @Transient
    val onlinePlayers = hashMapOf<Int, ConePlayer>()


    //读方块并执行操作
    fun readBlock(dimId: Int, chunkPosi: Int, doForEachBlock: (ConeBlockPos, Int, String?) -> Unit) {
        transaction {
            BlockStateTable.select {
                (BlockStateTable.chunkPos eq chunkPosi)
                    .and(BlockStateTable.roomId eq this@ConeRoom.id)
                    .and(BlockStateTable.dimId eq dimId)
            }.forEach {
                val bsid = it[BlockStateTable.blockStateId]
                val bpos = ConeBlockPos(it[BlockStateTable.blockPos])
                val tag = it[BlockStateTable.tag]
                doForEachBlock(bpos, bsid, tag)
            }
        }

    }

    //写方块
    fun writeBlock(packet: BlockDataC2CPacket) {
        transaction {
            BlockStateTable.upsert(BlockStateTable.roomId, BlockStateTable.dimId, BlockStateTable.blockPos) {
                it[roomId] = this@ConeRoom.id
                it[dimId] = packet.dimId
                it[blockPos] = packet.bpos.asLong
                it[chunkPos] = packet.bpos.chunkPos.asInt
                it[blockStateId] = packet.stateId
                it[tag] = packet.tag
            }
        }
    }


    //广播数据包（除了自己）
    fun broadcastPacket(packet: BufferWritable, sender: ConePlayer) {
        onlinePlayers.forEach {
            if (sender.addr != it.value.addr) {
                ConeNetSender.sendPacket(packet, it.value.addr)
            }
        }
    }


    /*fun readBlockStateId(dimId: Int, bpos: BlockPos): Int {
        return try {
            Files.readString(profilePath.resolve(getStateIdPath(dimId, bpos))).toInt()
        } catch (e: NoSuchFileException) {
            0
        }
    }*/
    override fun toString(): String {
        return "$name($id)"
    }

    companion object {
        private const val collectionName = "rooms"
        private val dbcl = DB.getCollection<ConeRoom>(collectionName)
        //全部运行中的房间 (rid to room)
        private val ridToRoom: MutableMap<Int, ConeRoom> = hashMapOf()

        //玩家id与正在游玩的房间 (pid to room)
        private val pidToPlayingRoom: MutableMap<Int, ConeRoom> = hashMapOf()

        //row->room
        private fun ofRow(row: RoomInfoRow): ConeRoom {
            return transaction {
                ConeRoom(
                    row.id.value,
                    row.name,
                    row.ownerId,
                    row.mcVersion,
                    row.isFabric,
                    row.isCreative,
                    row.blockStateAmount,
                    row.seed,
                    row.createTime,
                )
                /*RoomSavedChunksTable
                    .select { RoomSavedChunksTable.roomId eq row.id.value }
                    .map { ConeChunkPos(it[RoomSavedChunksTable.chunkPos]) }
                    .toMutableList()
                    .let {
                        return@transaction
                    }*/
            }

        }

        fun getPlayerPlayingRoom(pid: Int): ConeRoom? {
            return pidToPlayingRoom[pid]
        }

        //创建房间（数据库操作），返回房间ID
        private fun insert(room: ConeRoom): Int {
            return transaction {
                RoomInfoRow.new {
                    name = room.name
                    ownerId = room.ownerId
                    mcVersion = room.mcVer
                    isFabric = room.isFabric
                    isCreative = room.isCreative
                    blockStateAmount = room.blockStateAmount
                    seed = room.seed
                    createTime = room.createTime
                }.id.value
            }
        }

        //删除房间（数据库操作）
        private fun deleteById(rid: Int) {
            transaction {
                RoomSavedChunksTable.deleteWhere { roomId eq rid }
                BlockStateTable.deleteWhere { roomId eq rid }
                RoomInfoTable.deleteWhere { RoomInfoTable.id eq rid }
            }
        }

        //读取房间数据（数据库操作） ，不存在则null
        fun selectById(rid: Int): ConeRoom? {
            return transaction {
                ofRow(RoomInfoRow.findById(rid) ?: return@transaction null)
            }
        }

        //创建房间
        fun onCreate(
            player: ConePlayer,
            name: String,
            mcVersion: String,
            creative: Boolean,
            fabric: Boolean,
            blockStateAmount: Int
        ) {
            val ownRoom = getPlayerOwnRoom(player.id)
            if (ownRoom != null) {
                coneErrD(player, "建过房间了,ID=${ownRoom.id}")
                return
            }
            val room = ConeRoom(
                0,
                name,
                player.id,
                mcVersion,
                fabric,
                creative,
                blockStateAmount,
                Random.nextLong(),
                System.currentTimeMillis(),
            )
            val rid = insert(room)
            player.sendPacket(OkDataS2CPacket { it.writeVarInt(rid) })


        }

        //当玩家加入
        fun onPlayerJoin(player: ConePlayer, rid: Int) {
            val room = ridToRoom[rid] ?: run {
                selectById(rid)?.also {
                    ridToRoom += rid to it
                    logger.info { "$it 房间已启动" }
                } ?: let {
                    logger.warn { "$it 请求加入不存在的房间 $rid" }
                    return
                }
            }
            ConeNetSender.sendPacket(player.addr, room)
            room.onlinePlayers += player.id to player
            pidToPlayingRoom += player.id to room
            room.broadcastPacket(PlayerJoinedRoomS2CPacket(player.id, player.name), player)
            logger.info { "$player 加入了房间 $room" }
        }

        //当玩家删除
        fun onDelete(player: ConePlayer) = getPlayerOwnRoom(player.id) ?.run {
            deleteById(id)
            coneInfoT(player.addr,"成功删除房间")
        }?: let {
            coneErrD(player, "你没有房间")
            return
        }

        //当玩家读取
        fun onRetrieve(player: ConePlayer, rid: Int) {
            selectById(rid)?.also { player.sendPacket(it) } ?: run {
                coneErrD(player, "找不到房间$rid")
            }
        }

        //当玩家离开
        fun onPlayerLeave(player: ConePlayer) {
            pidToPlayingRoom[player.id]?.let {
                it.onlinePlayers -= player.id
                pidToPlayingRoom -= player.id
                it.broadcastPacket(PlayerLeftRoomS2CPacket(player.id), player)
                coneInfoT(player.addr, "已退出房间 ${it.name}")
            } ?: let {
                logger.warn {
                    "$player 没有加入任何房间就请求离开了"
                }
            }
        }


        //玩家已创建的房间
        fun getPlayerOwnRoom(pid: Int): ConeRoom? = transaction {
            RoomInfoRow.find { RoomInfoTable.ownerId eq pid }.firstOrNull()?.let { ofRow(it) }
        }


    }


}