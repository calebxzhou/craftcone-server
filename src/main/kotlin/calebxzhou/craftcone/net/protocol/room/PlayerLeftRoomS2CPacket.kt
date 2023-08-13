package calebxzhou.craftcone.net.protocol.room

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.Packet

/**
 * Created  on 2023-08-11,12:21.
 */
data class PlayerLeftRoomS2CPacket(
    val pid : Int
): Packet,BufferWritable{
    override fun write(buf: FriendlyByteBuf) {
        buf.writeVarInt(pid)
    }

}
