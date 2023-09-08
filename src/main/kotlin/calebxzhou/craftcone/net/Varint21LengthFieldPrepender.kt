package calebxzhou.craftcone.net

import calebxzhou.craftcone.util.ByteBufUt.getVarIntSize
import calebxzhou.craftcone.util.ByteBufUt.writeVarInt
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

/**
 * Created  on 2023-09-08,19:25.
 */
class Varint21LengthFieldPrepender : MessageToByteEncoder<ByteBuf>() {
    override fun encode(channelHandlerContext: ChannelHandlerContext, byteBuf: ByteBuf, byteBuf2: ByteBuf) {
        val i = byteBuf.readableBytes()
        val j = getVarIntSize(i)
        require(j <= 3) { "unable to fit $i into 3" }
        byteBuf2.ensureWritable(j + i)
        byteBuf2.writeVarInt(i)
        byteBuf2.writeBytes(byteBuf, byteBuf.readerIndex(), i)
    }

    companion object {
        /**
         * The max length, in number of bytes, of the prepending size var int permitted.
         * Has value {@value}.
         */
        private const val MAX_BYTES = 3
    }
}
