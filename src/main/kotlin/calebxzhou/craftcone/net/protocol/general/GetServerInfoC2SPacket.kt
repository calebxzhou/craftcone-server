package calebxzhou.craftcone.net.protocol.general

import calebxzhou.craftcone.net.ConeByteBuf
import calebxzhou.craftcone.net.coneSendPacket
import calebxzhou.craftcone.net.protocol.BeforeLoginProcessable
import calebxzhou.craftcone.net.protocol.BufferReadable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.server.CONF
import calebxzhou.craftcone.server.VERSION_NUM
import calebxzhou.craftcone.server.entity.ConePlayer
import java.net.InetSocketAddress

/**
 * Created  on 2023-08-26,21:07.
 */
class GetServerInfoC2SPacket : Packet,BeforeLoginProcessable{
    companion object : BufferReadable<GetServerInfoC2SPacket> {
        override fun read(buf: ConeByteBuf) = GetServerInfoC2SPacket()
    }

    override suspend fun process(ctx: ChannelHandlerContext) {
        coneSendPacket(clientAddress,ServerInfoS2CPacket(VERSION_NUM,ConePlayer.onlinePlayerCount,CONF.info.maxPlayerLimit,
            CONF.info.serverName, CONF.info.serverDescription, CONF.info.serverIconBase64))
    }
}
