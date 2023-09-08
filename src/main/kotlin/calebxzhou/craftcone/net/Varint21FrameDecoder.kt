package calebxzhou.craftcone.net

import calebxzhou.craftcone.util.ByteBufUt.readVarInt
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import io.netty.handler.codec.CorruptedFrameException

/**
 * Created  on 2023-09-08,19:25.
 */
/**
 * Counterpart to [Varint21LengthFieldPrepender]. Decodes each frame ("packet") by first reading its length and then its data.
 */
class Varint21FrameDecoder : ByteToMessageDecoder() {
    override fun decode(channelHandlerContext: ChannelHandlerContext, byteBuf: ByteBuf, list: MutableList<Any>) {
        byteBuf.markReaderIndex()
        val bs = ByteArray(3)
        for (i in bs.indices) {
            if (!byteBuf.isReadable) {
                byteBuf.resetReaderIndex()
                return
            }
            bs[i] = byteBuf.readByte()
            if (bs[i] >= 0) {
                val friendlyByteBuf = Unpooled.wrappedBuffer(bs)
                try {
                    val j = friendlyByteBuf.readVarInt()
                    if (byteBuf.readableBytes() >= j) {
                        list.add(byteBuf.readBytes(j))
                        return
                    }
                    byteBuf.resetReaderIndex()
                } finally {
                    friendlyByteBuf.release()
                }
                return
            }
        }
        throw CorruptedFrameException("length wider than 21-bit")
    }
}
