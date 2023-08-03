package calebxzhou.craftcone.net.protocol.account

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.OkDataPacket
import calebxzhou.craftcone.net.protocol.S2CPacket

/**
 * Created  on 2023-07-13,17:39.
 */
//登录响应
data class LoginS2CPacket(
    //是否登录成功
    override val ok: Boolean,
    //错误信息
    override val data: String,
) : S2CPacket,OkDataPacket {

    override fun write(buf: FriendlyByteBuf) {
        //for server
        buf.writeBoolean(ok)
        buf.writeUtf(data)
    }


}
