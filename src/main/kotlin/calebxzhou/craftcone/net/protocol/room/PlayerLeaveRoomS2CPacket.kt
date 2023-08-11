package calebxzhou.craftcone.net.protocol.room

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.Packet
import java.util.*

/**
 * Created  on 2023-08-11,12:21.
 */
data class PlayerLeaveRoomS2CPacket(
    val pid : UUID
): Packet,BufferWritable{
    override fun write(buf: FriendlyByteBuf) {
        buf.writeUUID(pid)
    }

}
