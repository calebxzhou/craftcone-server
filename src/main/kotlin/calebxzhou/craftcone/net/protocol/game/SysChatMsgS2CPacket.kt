package calebxzhou.craftcone.net.protocol.game

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.BufferWritable

/**
 * Created  on 2023-08-08,23:03.
 */
//聊天消息
data class SysChatMsgS2CPacket(
    val msg :String
):BufferWritable{
    override fun write(buf: FriendlyByteBuf) {
        buf.writeUtf(msg)
    }

}
