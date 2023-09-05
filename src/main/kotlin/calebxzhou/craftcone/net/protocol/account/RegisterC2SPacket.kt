package calebxzhou.craftcone.net.protocol.account

import calebxzhou.craftcone.net.ConeByteBuf
import calebxzhou.craftcone.net.protocol.BeforeLoginProcessable
import calebxzhou.craftcone.net.protocol.BufferReadable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.server.entity.ConePlayer
import java.net.InetSocketAddress

/**
 * Created  on 2023-07-21,10:37.
 */
data class RegisterC2SPacket(
    val pName:String,
    val pwd : String,
    val email:String,
): Packet, BeforeLoginProcessable {
    companion object : BufferReadable<RegisterC2SPacket> {
        override fun read(buf: ConeByteBuf) = RegisterC2SPacket( buf.readUtf(),buf.readUtf(),buf.readUtf())

    }

    override suspend fun process(ctx: ChannelHandlerContext) = ConePlayer.register(clientAddress,this)


}
