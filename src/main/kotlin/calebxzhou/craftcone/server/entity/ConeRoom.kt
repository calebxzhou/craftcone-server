package calebxzhou.craftcone.server.entity

import calebxzhou.craftcone.misc.UuidSerializer
import calebxzhou.craftcone.net.ConeNetSender
import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.room.RoomInfoS2CPacket
import calebxzhou.craftcone.server.DATA_DIR
import calebxzhou.craftcone.server.INFO_FILE
import calebxzhou.craftcone.server.logger
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.Path
import kotlin.random.Random

/**
 * Created  on 2023-08-03,13:20.
 */
@Serializable
data class ConeRoom(
    @Serializable(UuidSerializer::class)
    //房间ID
    val rid: UUID,
    //房间名
    val rName: String,
    @Serializable(UuidSerializer::class)
    //房主id
    val ownerId: UUID,
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

    ) {
    //房间档案path
    val profilePath = getProfilePath(rid)

    @Transient
    //正在游玩 当前房间的 玩家list
    val players = hashMapOf<UUID, ConePlayer>()
/*
    @Transient
    //临时id  to 玩家id （网络传输用）
    val tpidToPid = arrayListOf<UUID>()*/

    //保存方块状态&id
    fun saveBlockState(id: Int, state: String) {
        val statePath = profilePath.resolve("block_state/")
        Files.createDirectories(statePath)
        Files.writeString(statePath.resolve("$id"), state)
    }

    fun saveBlock(dimId: Int, bpos: Long, stateId: Int) {
        val path = profilePath.resolve("dim/$dimId/")
        Files.createDirectories(path)
        Files.writeString(path.resolve(bpos.toString(36)), "$stateId")
    }

    fun readBlock(dimId: Int, bpos: Long): Int {
        return Files.readString(profilePath.resolve("dim/$dimId/${bpos.toString(36)}")).toInt()
    }

    fun broadcastPacket(packet: BufferWritable) {
        players.forEach { ConeNetSender.sendPacket(packet, it.value.addr) }
    }

    val infoPacket
        get() = RoomInfoS2CPacket(rid, rName, isFabric, isCreative, blockStateAmount, seed)

    override fun toString(): String {
        return "$rName($rid)"
    }
    companion object {
        //全部在线房间
        private val onlineRooms = hashMapOf<UUID, ConeRoom>()

        //获取房间档案path
        fun getProfilePath(rid: UUID): Path {
            return Path("$DATA_DIR/rooms/$rid")
        }

        //房间是否在线
        fun isOnline(rid: UUID): Boolean {
            return onlineRooms.containsKey(rid)
        }

        //房间是否存在
        fun exists(rid: UUID): Boolean {
            return Files.exists(getProfilePath(rid))
        }

        //玩家加入房间
        fun joinRoom(player: ConePlayer, rid: UUID): ConeRoom? {
            val room = onlineRooms[rid] ?: let {
                logger.warn { "${player.pid} 请求加入不在线的房间$rid " }
                return null
            }
            room.players += Pair(player.pid, player)
            logger.info { "${player.pid} 加入了房间 $rid" }
            return room
        }


        //载入房间
        fun load(rid: UUID): ConeRoom {
            val infoStr = Files.readString(getProfilePath(rid).resolve("info.dat"))
            val room = Json.decodeFromString<ConeRoom>(infoStr)
            onlineRooms += Pair(rid, room)
            logger.info { "$rid ${room.rName} 房间已载入" }
            return room
        }

        fun create(
            player: ConePlayer,
            name: String,
            creative: Boolean,
            fabric: Boolean,
            blockStateAmount: Int
        ): ConeRoom {
            val coneRoom = ConeRoom(
                UUID.randomUUID(),
                name,
                player.pid,
                fabric,
                creative,
                blockStateAmount,
                Random.nextLong(),
                System.currentTimeMillis()
            )
            coneRoom.write()
            player.nowPlayingRoom = coneRoom
            return coneRoom
        }


    }

    fun write() {
        Files.createDirectories(profilePath)
        Files.writeString(profilePath.resolve(INFO_FILE), Json.encodeToString(this))
    }

    fun onPlayerLeave(conePlayer: ConePlayer) {
        players.remove(conePlayer.pid)
    }
}