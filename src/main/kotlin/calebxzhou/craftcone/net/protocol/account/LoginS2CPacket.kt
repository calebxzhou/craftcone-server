package calebxzhou.craftcone.net.protocol.account

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.net.protocol.ResultPacket

/**
 * Created  on 2023-07-13,17:39.
 */
//登录响应
data class LoginS2CPacket(
    //是否登录成功
    override val ok: Boolean,
    //错误信息
    override val data: String,
) : Packet, BufferWritable,ResultPacket {

    override fun write(buf: FriendlyByteBuf) {
        //for server
        buf.writeBoolean(ok)
        buf.writeUtf(data)
    }


}
