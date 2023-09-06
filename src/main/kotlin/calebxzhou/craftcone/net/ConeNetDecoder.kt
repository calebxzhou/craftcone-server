package calebxzhou.craftcone.net

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

class ConeNetDecoder : ByteToMessageDecoder() {
    override fun decode(ctx: ChannelHandlerContext, data: ByteBuf, out: MutableList<Any>) {
        out += ConePacketSet.create(data.readByte().toInt(), data)?: return
    }
}