package calebxzhou.craftcone.net.protocol.general

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.BeforeLoginProcessable
import calebxzhou.craftcone.net.protocol.BufferReadable
import calebxzhou.craftcone.net.protocol.Packet
import java.net.InetSocketAddress

/**
 * Created  on 2023-08-26,21:07.
 */
class GetServerInfoC2SPacket : Packet,BeforeLoginProcessable{
    companion object : BufferReadable<GetServerInfoC2SPacket> {
        override fun read(buf: FriendlyByteBuf) = GetServerInfoC2SPacket()
    }

    override suspend fun process(clientAddress: InetSocketAddress) {

    }
}
