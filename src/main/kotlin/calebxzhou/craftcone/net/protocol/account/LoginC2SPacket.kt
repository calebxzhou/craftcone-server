package calebxzhou.craftcone.net.protocol.account

import calebxzhou.craftcone.net.ConeNetSender
import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.*
import calebxzhou.craftcone.net.protocol.general.OkDataS2CPacket
import calebxzhou.craftcone.net.protocol.general.SysMsgS2CPacket
import calebxzhou.craftcone.server.entity.ConePlayer
import java.net.InetSocketAddress

/**
 * Created  on 2023-07-13,17:27.
 */
//玩家登录请求
data class LoginC2SPacket(
    //玩家UUID
    val pid: Int,
    //密码
    val pwd: String,
): Packet, BeforeLoginProcessable {
    companion object : BufferReadable<LoginC2SPacket>{
        override fun read(buf: FriendlyByteBuf): LoginC2SPacket {
            //for server
            return LoginC2SPacket(buf.readVarInt(),buf.readUtf())
        }

    }
    override fun process(clientAddress: InetSocketAddress) {
        if(ConePlayer.login(pid,pwd,clientAddress))
            ConeNetSender.sendPacket(OkDataS2CPacket(null),clientAddress)
        else
            ConeNetSender.sendPacket(SysMsgS2CPacket(MsgType.Toast,MsgLevel.Err,"用户UID和密码不匹配"),clientAddress)

    }


}