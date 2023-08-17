package calebxzhou.craftcone.net.protocol.general

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.MsgLevel
import calebxzhou.craftcone.net.protocol.MsgType
import calebxzhou.craftcone.net.protocol.Packet

/**
 * Created  on 2023-08-13,20:50.
 */
data class SysMsgS2CPacket(
    val type:MsgType,
    val lvl: MsgLevel,
    val msg:String
): Packet,BufferWritable {
    override fun write(buf: FriendlyByteBuf) {
        buf.writeVarInt(type.id)
        buf.writeVarInt(lvl.id)
        buf.writeUtf(msg)
    }
}
