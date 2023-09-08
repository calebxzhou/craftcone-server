package calebxzhou.craftcone.net.protocol.general

import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.Packet
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled

/**
 * Created  on 2023-08-15,8:47.
 */
//处理成功&数据
class OkDataS2CPacket: Packet,BufferWritable {
    val data:ByteBuf

    constructor(data: ByteBuf){
        this.data = data
    }
    constructor(dataWriter: (ByteBuf) -> Unit){
        val byteBuf = Unpooled.buffer()
        dataWriter(byteBuf)
        data = byteBuf
    }

    override fun write(buf: ByteBuf) {
        buf.writeBytes(data)
    }
}