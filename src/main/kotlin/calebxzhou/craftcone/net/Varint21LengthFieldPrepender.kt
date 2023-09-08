package calebxzhou.craftcone.net;

import calebxzhou.craftcone.util.ByteBufUt;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created  on 2023-09-08,19:25.
 */
public class Varint21LengthFieldPrepender extends MessageToByteEncoder<ByteBuf> {
    /**
     * The max length, in number of bytes, of the prepending size var int permitted.
     * Has value {@value}.
     */
    private static final int MAX_BYTES = 3;

    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, ByteBuf byteBuf2) {
        int i = byteBuf.readableBytes();
        int j = ByteBufUt.getVarIntSize(i);
        if (j > 3) {
            throw new IllegalArgumentException("unable to fit " + i + " into 3");
        } else {
            byteBuf2.ensureWritable(j + i);
            ByteBufUt.INSTANCE.writeVarInt(byteBuf2,i);
            byteBuf2.writeBytes(byteBuf, byteBuf.readerIndex(), i);
        }
    }
}
