package calebxzhou.craftcone.net

import calebxzhou.craftcone.server.logger
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

class ConeNetDecoder : ByteToMessageDecoder() {
    override fun decode(ctx: ChannelHandlerContext, data: ByteBuf, out: MutableList<Any>) {
        out += ConePacketSet.create(data.readByte().toInt(), ConeByteBuf(data))?: return
    }
}