package calebxzhou.craftcone.net.protocol.account

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.BeforeLoginProcessable
import calebxzhou.craftcone.net.protocol.BufferReadable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.server.entity.ConePlayer
import java.net.InetSocketAddress

/**
 * Created  on 2023-07-13,17:27.
 */
//玩家登录请求
data class LoginC2SPacket(
    //玩家uid
    val uid: Int,
    //密码
    val pwd: String,
) : Packet, BeforeLoginProcessable {
    companion object : BufferReadable<LoginC2SPacket> {
        override fun read(buf: FriendlyByteBuf) =
            LoginC2SPacket(buf.readVarInt(), buf.readUtf())

    }

    override fun process(clientAddress: InetSocketAddress) {
        ConePlayer.login(uid, pwd, clientAddress)
    }


}