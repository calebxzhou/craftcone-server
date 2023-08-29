package calebxzhou.craftcone.server.entity

import calebxzhou.craftcone.net.ConeNetSender.sendPacket
import calebxzhou.craftcone.net.ConeNetSender.sendPacketToAll
import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.coneErrD
import calebxzhou.craftcone.net.coneInfoT
import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.net.protocol.game.BlockDataC2CPacket
import calebxzhou.craftcone.net.protocol.game.PlayerJoinedRoomS2CPacket
import calebxzhou.craftcone.net.protocol.game.PlayerLeftRoomS2CPacket
import calebxzhou.craftcone.net.protocol.general.OkDataS2CPacket
import calebxzhou.craftcone.net.protocol.room.CreateRoomC2SPacket
import calebxzhou.craftcone.server.DB
import calebxzhou.craftcone.server.logger
import com.mongodb.client.model.Aggregates.*
import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Updates
import kotlinx.coroutines.flow.firstOrNull
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import kotlin.random.Random

/**
 * Created  on 2023-08-03,13:20.
 */
data class ConeRoom(
    @BsonId val id: ObjectId,
    val name: String,
    val owner: ConePlayer,
    val mcVer: String,
    val isCreative: Boolean,
    val blockStateAmount: Int,
    val seed: Long,
    val createTime: Long,
    val dimensions: List<ConeDimension> = arrayListOf()
) : Packet, BufferWritable {
    //dimension id to saved blocks
    @Transient
    val inRoomPlayers = hashMapOf<ObjectId, ConePlayer>()
    override fun write(buf: FriendlyByteBuf) {
        buf.writeUtf(id.toHexString())
        buf.writeUtf(name)
        buf.writeUtf(mcVer)
        buf.writeBoolean(isCreative)
        buf.writeVarInt(blockStateAmount)
        buf.writeLong(seed)
        buf.writeLong(createTime)
    }

    override fun toString(): String {
        return "$name($id)"
    }


    //读方块并执行操作
    suspend fun readBlock(dimId: Int, chunkPos: ConeChunkPos, doForEachBlock: (ConeBlockData) -> Unit) {
        dbcl.aggregate<ConeBlockData>(
            listOf(
                match(
                    and(
                        eq("_id", id),
                        eq("dimensions.dimId", dimId),
                        eq("dimensions.blockData.chunkPos", chunkPos)
                    )
                ),
                unwind("\$dimensions"),
                match(eq("dimensions.dimId", dimId)),
                unwind("\$dimensions.blockData"),
                replaceRoot("\$dimensions.blockData")
            )
        ).collect { doForEachBlock(it) }

    }

    //写方块
    suspend fun writeBlock(packet: BlockDataC2CPacket) = packet.run {
        ConeBlockData(
            bpos,
            bpos.chunkPos,
            stateId,
            tag
        ).run {
            dbcl.updateOne(
                eq("dimensions.dimId", dimId),
                Updates.push("dimensions.$.blockData", this)
            )
        }
    }


    companion object {
        const val collectionName = "rooms"
        private val dbcl = DB.getCollection<ConeRoom>(collectionName)

        //全部运行中的房间 (rid to room)
        private val runningRooms: MutableMap<ObjectId, ConeRoom> = hashMapOf()

        //玩家id与正在游玩的房间 (pid to room)
        private val uidPlayingRooms: MutableMap<ObjectId, ConeRoom> = hashMapOf()

        fun getPlayerPlayingRoom(uid: ObjectId): ConeRoom? = uidPlayingRooms[uid]

        //玩家已创建的房间
        suspend fun getPlayerOwnRoom(pid: ObjectId): ConeRoom? = dbcl.find(eq("owner._id", pid)).firstOrNull()
        suspend fun getById(id: ObjectId): ConeRoom? = dbcl.find(eq("_id", id)).firstOrNull()

        //当玩家读取
        suspend fun onPlayerGet(player: ConePlayer, rid: ObjectId) =
            getById(rid)?.run {
                player.sendPacket(this)
            } ?: run {
                coneErrD(player, "找不到房间$rid")
            }

        //创建房间
        suspend fun onPlayerCreate(
            player: ConePlayer,
            pkt: CreateRoomC2SPacket
        ) = getPlayerOwnRoom(player.id)?.run {
            coneErrD(player, "建过房间了,ID=$id")
        } ?: run {
            ConeRoom(
                ObjectId(),
                pkt.rName,
                player,
                pkt.mcVersion,
                pkt.isCreative,
                pkt.blockStateAmount,
                Random.nextLong(),
                System.currentTimeMillis()
            ).run {
                dbcl.insertOne(this)
                player.sendPacket(OkDataS2CPacket { it.writeObjectId(id) })
            }
        }

        //当玩家加入
        suspend fun onPlayerJoin(player: ConePlayer, rid: ObjectId): Unit = runningRooms[rid]?.run {
            inRoomPlayers += player.id to player
            uidPlayingRooms += player.id to this
            sendPacketToAll(player, PlayerJoinedRoomS2CPacket(player.id, player.name))
            logger.info { "$player 加入了房间 $this" }
        } ?: run {
            getById(rid)?.run {
                runningRooms += rid to this
                logger.info { "$this 房间已启动" }
                onPlayerJoin(player, rid)
            } ?: run {
                logger.warn { "$player 请求加入不存在的房间 $rid" }
            }
        }

        //当玩家离开
        fun onPlayerLeave(player: ConePlayer) = uidPlayingRooms[player.id]?.run {
            inRoomPlayers -= player.id
            uidPlayingRooms -= player.id
            sendPacketToAll(player, PlayerLeftRoomS2CPacket(player.id))
            coneInfoT(player.addr, "已退出房间 ${player.name}")
        }


        //当玩家删除
        suspend fun onPlayerDelete(player: ConePlayer) = getPlayerOwnRoom(player.id)?.run {
            if (dbcl.deleteOne(eq("_id", id)).deletedCount > 0) {
                coneInfoT(player.addr, "成功删除房间")
            }
        } ?: run {
            coneErrD(player, "你没有房间")
        }

    }
}