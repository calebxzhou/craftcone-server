package calebxzhou.craftcone.net.protocol.account

import calebxzhou.craftcone.net.protocol.BeforeLoginProcessable
import calebxzhou.craftcone.net.protocol.BufferReadable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.server.entity.ConePlayer
import calebxzhou.craftcone.util.ByteBufUt.readUtf
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

/**
 * Created  on 2023-07-13,17:27.
 */
data class LoginByNameC2SPacket(
    val name: String,
    val pwd: String,
) : Packet, BeforeLoginProcessable {
    companion object : BufferReadable<LoginByNameC2SPacket> {
        override fun read(buf: ByteBuf) = LoginByNameC2SPacket(buf.readUtf(), buf.readUtf())

    }

    override suspend fun process(ctx: ChannelHandlerContext) = ConePlayer.loginByName(ctx,this)


}