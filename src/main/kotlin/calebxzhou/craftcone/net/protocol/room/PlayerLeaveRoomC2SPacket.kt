package calebxzhou.craftcone.net.protocol.room

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.InRoomProcessable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.net.protocol.BufferReadable
import calebxzhou.craftcone.server.entity.ConePlayer
import calebxzhou.craftcone.server.entity.ConeRoom

/**
 * Created  on 2023-07-06,8:48.
 */
//玩家离开房间
class PlayerLeaveRoomC2SPacket : Packet, InRoomProcessable {

    companion object : BufferReadable<PlayerLeaveRoomC2SPacket>{
        override fun read(buf: FriendlyByteBuf): PlayerLeaveRoomC2SPacket {
            return PlayerLeaveRoomC2SPacket()
        }
    }

    override fun process(player: ConePlayer, playingRoom: ConeRoom) {
        player.leaveRoom()
    }

}