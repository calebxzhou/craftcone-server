package calebxzhou.craftcone.net.protocol.game

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.C2CPacket
import java.net.InetSocketAddress

/**
 * Created  on 2023-07-06,8:48.
 */
data class ChatC2CPacket (
    val senderName: String,
    val content: String,
): C2CPacket {


    companion object{
        fun read(buf: FriendlyByteBuf): ChatC2CPacket {
            return ChatC2CPacket(buf.readUtf(),buf.readUtf())
        }
    }

    override fun process(clientAddress: InetSocketAddress) {
        //TODO 转发给同一房间所有人
    }

    override fun write(buf: FriendlyByteBuf) {
        buf.writeUtf(senderName)
        buf.writeUtf(content)
    }


}