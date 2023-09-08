package calebxzhou.craftcone.net;

/**
 * Created  on 2023-09-08,19:25.
 */

import calebxzhou.craftcone.util.ByteBufUt;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;

import java.util.List;

/**
 * Counterpart to {@link Varint21LengthFieldPrepender}. Decodes each frame ("packet") by first reading its length and then its data.
 */
public class Varint21FrameDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        byteBuf.markReaderIndex();
        byte[] bs = new byte[3];

        for(int i = 0; i < bs.length; ++i) {
            if (!byteBuf.isReadable()) {
                byteBuf.resetReaderIndex();
                return;
            }

            bs[i] = byteBuf.readByte();
            if (bs[i] >= 0) {
                ByteBuf friendlyByteBuf = (Unpooled.wrappedBuffer(bs));

                try {
                    int j = ByteBufUt.INSTANCE.readVarInt(friendlyByteBuf);
                    if (byteBuf.readableBytes() >= j) {
                        list.add(byteBuf.readBytes(j));
                        return;
                    }

                    byteBuf.resetReaderIndex();
                } finally {
                    friendlyByteBuf.release();
                }

                return;
            }
        }

        throw new CorruptedFrameException("length wider than 21-bit");
    }
}
