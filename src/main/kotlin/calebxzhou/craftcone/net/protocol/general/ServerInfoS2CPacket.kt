package calebxzhou.craftcone.net.protocol.general

import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.server.CONF
import calebxzhou.craftcone.server.VERSION_NUM
import calebxzhou.craftcone.server.entity.ConeOnlinePlayer
import calebxzhou.craftcone.util.ByteBufUt.writeUtf
import calebxzhou.craftcone.util.ByteBufUt.writeVarInt
import io.netty.buffer.ByteBuf

/**
 * Created  on 2023-08-26,21:05.
 */
object ServerInfoS2CPacket  : Packet,BufferWritable{
    override fun write(buf: ByteBuf){
        buf.writeVarInt(VERSION_NUM)
            .writeVarInt(ConeOnlinePlayer.onlinePlayerCount)
            .writeVarInt(CONF.info.maxPlayerLimit)
            .writeUtf(CONF.info.serverName)
            .writeUtf(CONF.info.serverDescription)
            .writeUtf(CONF.info.serverIconBase64)
    }


}
