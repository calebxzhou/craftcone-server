package calebxzhou.craftcone.net.protocol.general

import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.util.ByteBufUt.writeUtf
import io.netty.buffer.ByteBuf

/**
 * Created  on 2023-08-30,8:58.
 */
data class CopyToClipboardS2CPacket(
    val content:String
) : Packet,BufferWritable{
    override fun write(buf: ByteBuf) {
        buf.writeUtf(content)
    }
}