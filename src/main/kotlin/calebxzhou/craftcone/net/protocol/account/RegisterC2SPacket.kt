package calebxzhou.craftcone.net.protocol.account

import calebxzhou.craftcone.net.protocol.BeforeLoginProcessable
import calebxzhou.craftcone.net.protocol.BufferReadable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.server.entity.ConePlayer
import calebxzhou.craftcone.util.ByteBufUt.readUtf
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

/**
 * Created  on 2023-07-21,10:37.
 */
data class RegisterC2SPacket(
    val pName:String,
    val pwd : String,
    val email:String,
): Packet, BeforeLoginProcessable {
    companion object : BufferReadable<RegisterC2SPacket> {
        override fun read(buf: ByteBuf) = RegisterC2SPacket( buf.readUtf(),buf.readUtf(),buf.readUtf())

    }

    override suspend fun process(ctx: ChannelHandlerContext) = ConePlayer.register(ctx,this)


}
