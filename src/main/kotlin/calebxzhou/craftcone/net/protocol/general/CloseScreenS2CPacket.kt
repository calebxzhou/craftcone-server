package calebxzhou.craftcone.net.protocol.general

import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.Packet
import io.netty.buffer.ByteBuf

/**
 * Created  on 2023-08-30,8:44.
 */
class CloseScreenS2CPacket(): Packet,BufferWritable {
    override fun write(buf: ByteBuf) {

    }
}