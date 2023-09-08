package calebxzhou.craftcone.net.protocol.general

import calebxzhou.craftcone.net.coneSendPacket
import calebxzhou.craftcone.net.protocol.BeforeLoginProcessable
import calebxzhou.craftcone.net.protocol.BufferReadable
import calebxzhou.craftcone.net.protocol.Packet
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

/**
 * Created  on 2023-08-26,21:07.
 */
class GetServerInfoC2SPacket : Packet,BeforeLoginProcessable{
    companion object : BufferReadable<GetServerInfoC2SPacket> {
        override fun read(buf: ByteBuf) = GetServerInfoC2SPacket()
    }

    override suspend fun process(ctx: ChannelHandlerContext) {
        coneSendPacket(ctx,ServerInfoS2CPacket)
    }
}
