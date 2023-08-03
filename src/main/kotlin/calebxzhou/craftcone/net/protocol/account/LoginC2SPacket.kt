package calebxzhou.craftcone.net.protocol.account

import calebxzhou.craftcone.net.ConeNetManager
import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.C2SPacket
import calebxzhou.craftcone.net.protocol.ReadablePacket
import calebxzhou.craftcone.server.entity.ConePlayer
import java.net.InetSocketAddress
import java.util.*

/**
 * Created  on 2023-07-13,17:27.
 */
//玩家登录请求
data class LoginC2SPacket(
    //玩家UUID
    val pid: UUID,
    //密码
    val pwd: String,
) : C2SPacket{
    companion object : ReadablePacket<LoginC2SPacket>{
        override fun read(buf: FriendlyByteBuf): LoginC2SPacket {
            //for server
            return LoginC2SPacket(buf.readUUID(),buf.readUtf())
        }

    }
    override fun process(clientAddress: InetSocketAddress) {
        val player = ConePlayer(pid,pwd,clientAddress)
        val packet = if(ConePlayer.login(player))
            LoginS2CPacket(true,"")
        else
            LoginS2CPacket(false,"密码错误")
        ConeNetManager.sendPacket(packet,clientAddress)


    }


}