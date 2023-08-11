package calebxzhou.craftcone.net.protocol.room

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.net.protocol.ResultPacket

/**
 * Created  on 2023-08-01,19:18.
 */
data class PlayerDeleteRoomS2CPacket(
    override val ok: Boolean,
    //成功rid 失败信息
    override val data: String,
) : Packet, BufferWritable, ResultPacket {
    override fun write(buf: FriendlyByteBuf) {
        buf.writeBoolean(ok)
        buf.writeUtf(data)
    }

}
