package calebxzhou.craftcone.net

import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.server.logger
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

class ConeNetEncoder : MessageToByteEncoder<BufferWritable>() {
    override fun encode(ctx: ChannelHandlerContext, packet: BufferWritable, data: ByteBuf) {
        ConePacketSet.getPacketId(packet.javaClass)?.let { packetId->
            data.writeByte(packetId)
            packet.write(data)
            logger.debug { "Packet ${packet.javaClass.simpleName} encoding with ID $packetId, size ${data.readableBytes()}" }
        }?: let{
            logger.error("找不到$packet 对应的包ID")
        }
    }
}