package calebxzhou.craftcone.net.protocol.account

import calebxzhou.craftcone.net.ConeNetManager
import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.BeforeLoginProcessable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.net.protocol.BufferReadable
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
): Packet, BeforeLoginProcessable {
    companion object : BufferReadable<LoginC2SPacket>{
        override fun read(buf: FriendlyByteBuf): LoginC2SPacket {
            //for server
            return LoginC2SPacket(buf.readUUID(),buf.readUtf())
        }

    }
    override fun process(clientAddress: InetSocketAddress) {
        val packet = if(ConePlayer.login(pid,pwd,clientAddress))
            LoginS2CPacket(true,"")
        else
            LoginS2CPacket(false,"密码错误")
        ConeNetManager.sendPacket(packet,clientAddress)


    }


}