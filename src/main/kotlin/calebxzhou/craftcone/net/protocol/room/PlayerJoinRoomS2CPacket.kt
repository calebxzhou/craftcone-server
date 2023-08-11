package calebxzhou.craftcone.net.protocol.room

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.Packet
import java.util.*

/**
 * Created  on 2023-08-10,8:24.
 */
data class PlayerJoinRoomS2CPacket(
    val pid: UUID,
    val pName: String
) : Packet, BufferWritable{
    override fun write(buf: FriendlyByteBuf) {
        buf.writeUUID(pid)
        buf.writeUtf(pName)
    }
}