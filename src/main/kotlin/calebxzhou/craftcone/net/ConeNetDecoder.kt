package calebxzhou.craftcone.net

import calebxzhou.craftcone.server.logger
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

class ConeNetDecoder : ByteToMessageDecoder() {
    override fun decode(ctx: ChannelHandlerContext, datas: ByteBuf, out: MutableList<Any>) {
        val packetId = datas.readByte().toInt()
        logger.debug{ "decoding data, ID = $packetId,  size = ${datas.readableBytes()}" }
        out += ConePacketSet.create(packetId, datas)?: return
    }
}