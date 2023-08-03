package calebxzhou.craftcone.server

import calebxzhou.craftcone.server.entity.ConeRoom
import java.net.InetSocketAddress
import java.util.*

/**
 * Created  on 2023-08-03,19:04.
 */
object RoomManager {
    //全部在线房间
    val onlineRooms = hashMapOf<UUID,ConeRoom>()
    //玩家ip to 玩家正在游玩的房间
    val addrToPlayingRoom = hashMapOf<InetSocketAddress, ConeRoom>()


}