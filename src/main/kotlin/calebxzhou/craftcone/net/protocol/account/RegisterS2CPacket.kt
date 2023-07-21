package calebxzhou.craftcone.net.protocol.account

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.S2CPacket

/**
 * Created  on 2023-07-13,17:39.
 */
//响应
data class RegisterS2CPacket(
    //是否成功
    val isSuccess: Boolean,
    //错误信息
    val msg: String,
) : S2CPacket {

    override fun write(buf: FriendlyByteBuf) {
        //for server
        buf.writeBoolean(isSuccess)
        buf.writeUtf(msg)
    }


}
