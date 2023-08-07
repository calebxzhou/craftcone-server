package calebxzhou.craftcone.net.protocol

import calebxzhou.craftcone.server.entity.Player
import calebxzhou.craftcone.server.entity.Room

/**
 * Created  on 2023-08-04,20:21.
 */
//适用于房间内的数据包
interface InRoomProcessable {
    //处理数据
    fun process(player: Player, playingRoom: Room)
}