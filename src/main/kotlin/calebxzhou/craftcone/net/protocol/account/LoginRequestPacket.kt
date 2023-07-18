package calebxzhou.craftcone.net.protocol.account

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.ConeOutGamePacket
import calebxzhou.craftcone.net.protocol.ReadablePacket
import java.util.*

/**
 * Created  on 2023-07-13,17:27.
 */
//玩家登录请求
data class LoginRequestPacket(
    //玩家UUID
    val pid: UUID,
    //密码
    val pwd: String,
) : ConeOutGamePacket{
    companion object : ReadablePacket{
        override fun read(buf: FriendlyByteBuf): LoginRequestPacket {
            //for server
            return LoginRequestPacket(buf.readUUID(),buf.readUtf())
        }

    }
    override fun process() {
        //for server
    }

    override fun write(buf: FriendlyByteBuf){
        //for client
        buf.writeUUID(pid)
        buf.writeUtf(pwd)
    }

}