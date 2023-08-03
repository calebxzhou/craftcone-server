package calebxzhou.craftcone.server.entity

import calebxzhou.craftcone.misc.UuidSerializer
import calebxzhou.craftcone.server.DATA_DIR
import calebxzhou.craftcone.server.logger
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.InetSocketAddress
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.Path

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
    val ownerUid: UUID,
    //mod加载器？Fabric：Forge
    val isFabric: Boolean,
    //创造
    val isCreative: Boolean,
    //方块状态数量
    val blockStateAmount: Int,
    //地图种子
    val seed: Long,

) {
    //房间档案path
    val profilePath = getProfilePath(rid)
    //正在游玩 当前房间的 玩家list
    val players = arrayListOf<ConePlayer>()




    companion object{
        //全部在线房间
        private val onlineRooms = hashMapOf<UUID,ConeRoom>()
        //玩家ip to 玩家正在游玩的房间
        val addrToPlayingRoom = hashMapOf<InetSocketAddress, ConeRoom>()

        //获取房间档案path
        fun getProfilePath(rid:UUID): Path {
            return  Path("$DATA_DIR/rooms/$rid")
        }
        //房间是否在线
        fun isOnline(rid: UUID) :Boolean{
            return onlineRooms.containsKey(rid)
        }
        //房间是否存在
        fun exists(rid: UUID) :Boolean{
            return Files.exists(getProfilePath(rid))
        }

        //玩家加入房间
        fun playerJoinRoom(player: ConePlayer, rid: UUID) {
            val room = onlineRooms[rid]?:let{
                logger.warn { "${player.pid} 请求加入不在线的房间$rid " }
                return
            }
            room.players += player
            addrToPlayingRoom += Pair(player.addr,room)
            logger.info { "${player.pid} 加入了房间 $rid" }
        }

        //载入房间
        fun load(rid: UUID) :ConeRoom{
            val infoStr = Files.readString(getProfilePath(rid).resolve("info.dat"))
            val room = Json.decodeFromString<ConeRoom>(infoStr)
            onlineRooms += Pair(rid,room)
            logger.info { "$rid ${room.rName} 房间已载入" }
            return room
        }

    }
}