package calebxzhou.craftcone.net.protocol.room

import calebxzhou.craftcone.net.protocol.BufferReadable
import calebxzhou.craftcone.net.protocol.InRoomProcessable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.server.entity.ConeOnlinePlayer
import calebxzhou.craftcone.server.entity.ConeRoom
import io.netty.buffer.ByteBuf

/**
 * Created  on 2023-07-06,8:48.
 */
//玩家离开房间
class LeaveRoomC2SPacket : Packet, InRoomProcessable {

    companion object : BufferReadable<LeaveRoomC2SPacket>{
        override fun read(buf: ByteBuf): LeaveRoomC2SPacket {
            return LeaveRoomC2SPacket()
        }
    }

    override suspend fun process(player: ConeOnlinePlayer, playingRoom: ConeRoom) {
        ConeRoom.onPlayerLeave(player)
    }

}