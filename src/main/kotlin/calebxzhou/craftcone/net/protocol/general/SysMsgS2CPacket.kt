package calebxzhou.craftcone.net.protocol.general

import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.MsgLevel
import calebxzhou.craftcone.net.protocol.MsgType
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.util.ByteBufUt.writeUtf
import calebxzhou.craftcone.util.ByteBufUt.writeVarInt
import io.netty.buffer.ByteBuf

/**
 * Created  on 2023-08-13,20:50.
 */
data class SysMsgS2CPacket(
    val type:MsgType,
    val level: MsgLevel,
    val msg:String
): Packet,BufferWritable {
    override fun write(buf: ByteBuf) {
        buf.writeVarInt(type.id)
        buf.writeVarInt(level.id)
        buf.writeUtf(msg)
    }
}
