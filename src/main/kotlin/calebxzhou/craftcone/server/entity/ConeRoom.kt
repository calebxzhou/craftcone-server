package calebxzhou.craftcone.server.entity

import calebxzhou.craftcone.net.ConeNetSender
import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.coneErrD
import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.net.protocol.game.BlockDataC2CPacket
import calebxzhou.craftcone.net.protocol.game.PlayerJoinedRoomS2CPacket
import calebxzhou.craftcone.net.protocol.game.PlayerLeftRoomS2CPacket
import calebxzhou.craftcone.net.protocol.general.OkDataS2CPacket
import calebxzhou.craftcone.server.logger
import calebxzhou.craftcone.server.table.BlockStateTable
import calebxzhou.craftcone.server.table.RoomInfoRow
import calebxzhou.craftcone.server.table.RoomInfoTable
import calebxzhou.craftcone.server.table.RoomSavedChunksTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert
import kotlin.random.Random

/**
 * Created  on 2023-08-03,13:20.
 */
data class ConeRoom(
    //房间ID
    val id: Int,
    //房间名
    val name: String,
    //房主id
    val ownerId: Int,
    //mc版本
    val mcVersion: String,
    //mod加载器？Fabric：Forge
    val isFabric: Boolean,
    //创造
    val isCreative: Boolean,
    //方块状态数量
    val blockStateAmount: Int,
    //地图种子
    val seed: Long,
    //创建时间
    val createTime: Long,
    //保存区块
    val savedChunks: MutableList<ConeChunkPos>,
) : Packet, BufferWritable {

    //写入到ByteBuf
    override fun write(buf: FriendlyByteBuf) {
        buf.writeVarInt(id)
        buf.writeUtf(name)
        buf.writeVarInt(ownerId)
        buf.writeUtf(mcVersion)
        buf.writeBoolean(isFabric)
        buf.writeBoolean(isCreative)
        buf.writeVarInt(blockStateAmount)
        buf.writeLong(seed)
        buf.writeLong(createTime)
        buf.writeVarIntArray(savedChunks.map { it.asInt }.toIntArray())
    }

    //房间在线玩家list
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
                tag?.isNotBlank()?.let { bl ->
                    if(bl){
                        logger.info { "读取方块${bpos} ${bpos.chunkPos.x} ${bpos.chunkPos.z}（$bsid $tag）" }
                    }
                }

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
                    arrayListOf()
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
                    mcVersion = room.mcVersion
                    isFabric = room.isFabric
                    isCreative = room.isCreative
                    blockStateAmount = room.blockStateAmount
                    seed = room.seed
                    createTime = room.createTime
                }.id.value
            }
        }
        //删除房间（数据库操作）
        private fun deleteById(rid: Int){
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
                arrayListOf()
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
        fun onDelete(player: ConePlayer) {
            val room = getPlayerOwnRoom(player.id) ?: let {
                coneErrD(player, "你没有房间")
                return
            }
            deleteById(room.id)
            player.sendPacket(OkDataS2CPacket())
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
            } ?: let {
                logger.warn { "$player 没有加入任何房间就请求离开了" }
                return
            }
        }


        //玩家已创建的房间
        fun getPlayerOwnRoom(pid: Int): ConeRoom? {
            return RoomInfoRow.find { RoomInfoTable.ownerId eq pid }.firstOrNull()?.let { ofRow(it) }
        }



    }


}