package calebxzhou.craftcone.net.protocol.general

import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.Packet
import io.netty.buffer.ByteBuf

/**
 * Created  on 2023-08-15,8:47.
 */
//处理成功&数据
data class OkDataS2CPacket(val dataWriter: ((ByteBuf) -> Unit)? = null): Packet,BufferWritable {

    override fun write(buf: ByteBuf) {
        dataWriter?.let { it(buf) }
    }
}