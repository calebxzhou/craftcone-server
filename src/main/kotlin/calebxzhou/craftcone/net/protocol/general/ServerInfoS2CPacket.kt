package calebxzhou.craftcone.net.protocol.general

import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.server.VERSION_NUM
import calebxzhou.craftcone.util.ByteBufUt.writeUtf
import calebxzhou.craftcone.util.ByteBufUt.writeVarInt
import io.netty.buffer.ByteBuf

/**
 * Created  on 2023-08-26,21:05.
 */
data class ServerInfoS2CPacket(
    val version: Int = VERSION_NUM,
    val onlinePlayers:Int,
    val maxPlayers:Int,
    val name:String,
    val descr:String,
    //base64-encoded
    val icon:String,
) : Packet,BufferWritable{
    override fun write(buf: ByteBuf){
        buf.writeVarInt(version)
            .writeVarInt(onlinePlayers)
            .writeVarInt(maxPlayers)
            .writeUtf(name)
            .writeUtf(descr)
            .writeUtf(icon)
    }


}
