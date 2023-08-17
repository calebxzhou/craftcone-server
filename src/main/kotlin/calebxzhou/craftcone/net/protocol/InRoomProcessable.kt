package calebxzhou.craftcone.net.protocol

import calebxzhou.craftcone.server.entity.ConePlayer
import calebxzhou.craftcone.server.entity.ConeRoom

/**
 * Created  on 2023-08-04,20:21.
 */
//适用于房间内的数据包
interface InRoomProcessable {
    //处理数据
    fun process(player: ConePlayer, playingRoom: ConeRoom)
}