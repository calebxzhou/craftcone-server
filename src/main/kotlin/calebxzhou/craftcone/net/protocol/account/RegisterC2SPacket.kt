package calebxzhou.craftcone.net.protocol.account

import calebxzhou.craftcone.net.FriendlyByteBuf
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
): Packet, BeforeLoginProcessable {
    companion object : BufferReadable<RegisterC2SPacket> {
        override fun read(buf: FriendlyByteBuf) = RegisterC2SPacket( buf.readUtf(),buf.readUtf())

    }

    override fun process(clientAddress: InetSocketAddress) {
        ConePlayer.register(pwd,pName,clientAddress)
    }

}
