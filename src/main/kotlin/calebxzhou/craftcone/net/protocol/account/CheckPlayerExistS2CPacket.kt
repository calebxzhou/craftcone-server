package calebxzhou.craftcone.net.protocol.account

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.Packet

/**
 * Created  on 2023-07-18,7:46.
 */
data class CheckPlayerExistS2CPacket(
    val exists: Boolean
): Packet, BufferWritable {


    override fun write(buf: FriendlyByteBuf) {
        //server
        buf.writeBoolean(exists)
    }

}
