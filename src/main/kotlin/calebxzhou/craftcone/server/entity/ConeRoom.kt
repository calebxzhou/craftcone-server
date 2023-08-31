package calebxzhou.craftcone.server.entity

import calebxzhou.craftcone.net.ConeByteBuf
import calebxzhou.craftcone.net.ConeNetSender.sendPacket
import calebxzhou.craftcone.net.ConeNetSender.sendPacketToAll
import calebxzhou.craftcone.net.coneErrDialog
import calebxzhou.craftcone.net.coneInfoToast
import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.net.protocol.game.BlockDataC2CPacket
import calebxzhou.craftcone.net.protocol.game.PlayerJoinedRoomS2CPacket
import calebxzhou.craftcone.net.protocol.game.PlayerLeftRoomS2CPacket
import calebxzhou.craftcone.net.protocol.general.CloseScreenS2CPacket
import calebxzhou.craftcone.net.protocol.general.CopyToClipboardS2CPacket
import calebxzhou.craftcone.net.protocol.general.OkDataS2CPacket
import calebxzhou.craftcone.net.protocol.room.CreateRoomC2SPacket
import calebxzhou.craftcone.server.DB
import calebxzhou.craftcone.server.logger
import calebxzhou.craftcone.util.bsonOf
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Updates.addToSet
import com.mongodb.client.model.Updates.pull
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
    //dimension id to saved blocks
    val blockData: List<ConeBlockData>? = arrayListOf()
) : Packet, BufferWritable {

    @Transient
    val inRoomPlayers = hashMapOf<ObjectId, ConePlayer>()

    data class ReadBlockData(@BsonId val id: ObjectId, val blockData: List<ConeBlockData>? = arrayListOf())

    override fun write(buf: ConeByteBuf) {
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


    //读方块并执行操作
    suspend fun readBlock(dimId: Int, chunkPos: ConeChunkPos, doForEachBlock: (ConeBlockData) -> Unit) =
        bsonOf(
            """
              {
                  %project: {
                      blockData: {
                          %filter: {
                              input: "%blockData",
                              as: "block",
                              cond: { %and: [
                                      { %eq: ["%%block.chunkPos", ${chunkPos.asInt}] },
                                      { %eq: ["%%block.dimId", $dimId] }
                                  ]
                              }
                          }
                      }
                  }
              }
        """.trimIndent()
        )
            .let { listOf(it) }
            .let { pipeline ->
                DB.getCollection<ReadBlockData>(collectionName)
                    .aggregate(pipeline)
                    .firstOrNull()
                    ?.blockData
                    ?.forEach(doForEachBlock)
            }


    suspend fun writeBlock(packet: BlockDataC2CPacket) = packet.run {
        ConeBlockData(
            dimId,
            bpos.asLong,
            bpos.chunkPos.asInt,
            stateId,
            tag
        ).run {
            dbcl.updateOne(
                eq("_id", id),
                pull("blockPos", blockPos)
                //pushEach("blockData",writeBlockQueue)
            )
            dbcl.updateOne(
                eq("_id", id),
                addToSet("blockData", this)
            )
        }

    }

    companion object {
        const val collectionName = "rooms"
        val dbcl = DB.getCollection<ConeRoom>(collectionName)

        //全部运行中的房间 (rid to room)
        private val runningRooms: MutableMap<ObjectId, ConeRoom> = hashMapOf()

        //玩家id与正在游玩的房间 (pid to room)
        private val uidPlayingRooms: MutableMap<ObjectId, ConeRoom> = hashMapOf()

        fun getPlayerPlayingRoom(uid: ObjectId) = uidPlayingRooms[uid]

        fun getRunningRoom(rid: ObjectId) = runningRooms[rid]

        //玩家已创建的房间
        suspend fun getPlayerOwnRoom(pid: ObjectId): ConeRoom? = dbcl.find(eq("owner._id", pid)).firstOrNull()
        suspend fun getById(id: ObjectId): ConeRoom? = dbcl.find(eq("_id", id)).firstOrNull()

        //当玩家读取
        suspend fun onPlayerGet(player: ConePlayer, rid: ObjectId) =
            getById(rid)?.run {
                player.sendPacket(this)
            } ?: run {
                coneErrDialog(player, "找不到房间$rid")
            }

        //创建房间
        suspend fun onPlayerCreate(
            player: ConePlayer,
            pkt: CreateRoomC2SPacket
        ) = getPlayerOwnRoom(player.id)?.run {
            coneErrDialog(player, "建过房间了,ID=$id")
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
            logger.info { "$player 加入了房间 $this" }
            inRoomPlayers += player.id to player
            uidPlayingRooms += player.id to this
            sendPacketToAll(player, PlayerJoinedRoomS2CPacket(player.id, player.name))
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
            coneInfoToast(player.addr, "已退出房间 ${player.name}")
        }


        //当玩家删除
        suspend fun onPlayerDelete(player: ConePlayer) = getPlayerOwnRoom(player.id)?.run {
            if (dbcl.deleteOne(eq("_id", id)).deletedCount > 0) {
                coneInfoToast(player.addr, "成功删除房间")
                player.sendPacket(CloseScreenS2CPacket())
            }
        } ?: run {
            coneErrDialog(player, "你没有房间")
        }

        suspend fun onPlayerGetMy(player: ConePlayer) = getPlayerOwnRoom(player.id)?.run {
            player.sendPacket(CopyToClipboardS2CPacket(id.toHexString()))
            coneInfoToast(player.addr, "已经复制你的房间ID。")
        } ?: run {
            coneErrDialog(player, "你没有房间")
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