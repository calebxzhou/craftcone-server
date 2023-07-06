package calebxzhou.craftcone.server.net

import calebxzhou.craftcone.server.net.FriendlyByteBuf
import calebxzhou.craftcone.server.net.LOG
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

/**
 * Created  on 2023-07-05,11:05.
 */
class ConePacketDecoder : ByteToMessageDecoder() {
    override fun decode(ctx: ChannelHandlerContext?, input: ByteBuf, out: MutableList<Any>) {
        val size = input.readableBytes()
        if(size<=0) return
        val buf = FriendlyByteBuf(input)
        val packetId = buf.readVarInt()
        LOG.info("packetID: $packetId")
    }
}