package calebxzhou.craftcone.net.protocol.room

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.Packet

/**
 * Created  on 2023-08-10,8:24.
 */
data class PlayerJoinedRoomS2CPacket(
    val pid: Int,
    val pName: String
) : Packet, BufferWritable{
    override fun write(buf: FriendlyByteBuf) {
        buf.writeVarInt(pid)
        buf.writeUtf(pName)
    }
}