package calebxzhou.craftcone.server.entity

import calebxzhou.craftcone.net.ConeNetSender.sendPacket
import calebxzhou.craftcone.net.ConeNetSender.sendPacketToAll
import calebxzhou.craftcone.net.coneErrDialog
import calebxzhou.craftcone.net.coneInfoToast
import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.net.protocol.game.PlayerJoinedRoomS2CPacket
import calebxzhou.craftcone.net.protocol.game.PlayerLeftRoomS2CPacket
import calebxzhou.craftcone.net.protocol.general.CloseScreenS2CPacket
import calebxzhou.craftcone.net.protocol.general.CopyToClipboardS2CPacket
import calebxzhou.craftcone.net.protocol.general.OkDataS2CPacket
import calebxzhou.craftcone.net.protocol.room.CreateRoomC2SPacket
import calebxzhou.craftcone.server.DB
import calebxzhou.craftcone.server.logger
import calebxzhou.craftcone.util.ByteBufUt.writeObjectId
import calebxzhou.craftcone.util.ByteBufUt.writeUtf
import calebxzhou.craftcone.util.ByteBufUt.writeVarInt
import com.mongodb.client.model.Filters.eq
import io.netty.buffer.ByteBuf
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
    val createTime: Long
) : Packet, BufferWritable {

    @Transient
    val inRoomPlayers = hashMapOf<ObjectId, ConeOnlinePlayer>()

    data class ReadBlockData(@BsonId val id: ObjectId, val blockData: List<ConeBlockData>? = arrayListOf())

    override fun write(buf: ByteBuf) {
        buf.writeObjectId(id)
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


    companion object {
        const val collectionName = "rooms"
        val dbcl = DB.getCollection<ConeRoom>(collectionName)


        //玩家id与正在游玩的房间 (pid to room)
        private val uidPlayingRooms: MutableMap<ObjectId, ConeRoom> = hashMapOf()
        val onlineRooms = hashMapOf<ObjectId,ConeRoom>()

        fun getPlayerPlayingRoom(uid: ObjectId) = uidPlayingRooms[uid]


        //玩家已创建的房间
        suspend fun getPlayerOwnRoom(pid: ObjectId): ConeRoom? = dbcl.find(eq("owner._id", pid)).firstOrNull()
        suspend fun getById(id: ObjectId): ConeRoom? = onlineRooms[id]?:dbcl.find(eq("_id", id)).firstOrNull()


        suspend fun onPlayerGet(player: ConeOnlinePlayer, rid: ObjectId) =
            getById(rid)?.run {
                player.sendPacket(this)
            } ?: run {
                coneErrDialog(player.ctx, "找不到房间$rid")
            }

        suspend fun create(id: ObjectId, player: ConeOnlinePlayer, pkt: CreateRoomC2SPacket) {
            ConeRoom(
                id,
                pkt.rName,
                player.data,
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

        //创建房间
        suspend fun onPlayerCreate(
            player: ConeOnlinePlayer,
            pkt: CreateRoomC2SPacket
        ) = getPlayerOwnRoom(player.data.id)?.run {
            coneErrDialog(player.ctx, "建过房间了,ID=$id")
        } ?: run {
            create(ObjectId(), player, pkt)
        }

        //当玩家加入
        suspend fun onPlayerJoin(player: ConeOnlinePlayer, rid: ObjectId) = getById(rid)?.run {
            logger.info { "$player 加入了房间 $this" }
            inRoomPlayers += player.data.id to player
            uidPlayingRooms += player.data.id to this
            sendPacketToAll(player, PlayerJoinedRoomS2CPacket(player.data.id, player.data.name))
        } ?: run {
                logger.warn { "$player 请求加入不存在的房间 $rid" }
        }

        //当玩家离开getById(rid)
        fun onPlayerLeave(player: ConeOnlinePlayer) = uidPlayingRooms[player.data.id]?.run {
            inRoomPlayers -= player.data.id
            uidPlayingRooms -= player.data.id
            sendPacketToAll(player, PlayerLeftRoomS2CPacket(player.data.id))
            coneInfoToast(player.ctx, "已退出房间 ${player.data.name}")
            if(inRoomPlayers.isEmpty()){
                onlineRooms -= id
                logger.info { "$this 房间没人了，即将关闭" }
            }
        }


        //当玩家删除
        suspend fun onPlayerDelete(player: ConeOnlinePlayer) = getPlayerOwnRoom(player.data.id)?.run {
            if (dbcl.deleteOne(eq("_id", id)).deletedCount > 0) {
                ConeBlockData.clearByRoomId(id)
                coneInfoToast(player.ctx, "成功删除房间")
                player.sendPacket(CloseScreenS2CPacket())
            }
        } ?: run {
            coneErrDialog(player.ctx, "你没有房间")
        }

        suspend fun onPlayerGetMy(player: ConeOnlinePlayer) = getPlayerOwnRoom(player.data.id)?.run {
            player.sendPacket(CopyToClipboardS2CPacket(id.toHexString()))
            coneInfoToast(player.ctx, "已经复制你的房间ID。")
        } ?: run {
            coneErrDialog(player.ctx, "你没有房间")
        }

    }
}

/*
     db.rooms.aggregate([
  {
      $project: {
          name: 1,
          blockData: {
              $filter: {
                  input: "$blockData",
                  as: "block",
                  cond: { $eq: ["$$block.chunkPos", 65536] }
              }
          }
      }
  }
])
     */