package calebxzhou.craftcone.net.protocol.account

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.ConeOutGamePacket
import calebxzhou.craftcone.net.protocol.ReadablePacket

/**
 * Created  on 2023-07-13,17:39.
 */
//登录响应
data class LoginResponsePacket(
    //是否登录成功
    val isSuccess: Boolean,
    //错误信息
    val msg: String,
) : ConeOutGamePacket{
    companion object : ReadablePacket{
        override fun read(buf: FriendlyByteBuf): LoginResponsePacket {
            //for client
            return LoginResponsePacket(buf.readBoolean(),buf.readUtf())
        }

    }

    override fun process() {
        //for client
    }

    override fun write(buf: FriendlyByteBuf) {
        //for server
        buf.writeBoolean(isSuccess)
        buf.writeUtf(msg)
    }


}
