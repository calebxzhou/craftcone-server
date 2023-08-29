package calebxzhou.craftcone.net.protocol.room

import calebxzhou.craftcone.net.ConeByteBuf
import calebxzhou.craftcone.net.protocol.AfterLoginProcessable
import calebxzhou.craftcone.net.protocol.BufferReadable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.server.entity.ConePlayer
import calebxzhou.craftcone.server.entity.ConeRoom

/**
 * Created  on 2023-08-11,20:39.
 */
class DelRoomC2SPacket : Packet,AfterLoginProcessable{
    companion object : BufferReadable<DelRoomC2SPacket>{
        override fun read(buf: ConeByteBuf) = DelRoomC2SPacket()
    }
    override suspend fun process(player: ConePlayer) {
        ConeRoom.onPlayerDelete(player)
    }

}
