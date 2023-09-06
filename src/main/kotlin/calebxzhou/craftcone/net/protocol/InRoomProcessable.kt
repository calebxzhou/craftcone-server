package calebxzhou.craftcone.net.protocol

import calebxzhou.craftcone.server.entity.ConeOnlinePlayer
import calebxzhou.craftcone.server.entity.ConeRoom

/**
 * Created  on 2023-08-04,20:21.
 */
//适用于房间内的数据包
interface InRoomProcessable {
    //处理数据
    suspend fun process(player: ConeOnlinePlayer, playingRoom: ConeRoom)
}