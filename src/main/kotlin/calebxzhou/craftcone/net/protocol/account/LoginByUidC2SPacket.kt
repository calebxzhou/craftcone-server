package calebxzhou.craftcone.net.protocol.account

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.BeforeLoginProcessable
import calebxzhou.craftcone.net.protocol.BufferReadable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.server.entity.ConePlayer
import org.bson.types.ObjectId
import java.net.InetSocketAddress

/**
 * Created  on 2023-07-13,17:27.
 */
//玩家登录请求
data class LoginByUidC2SPacket(
    //玩家uid
    val uid: ObjectId,
    //密码
    val pwd: String,
) : Packet, BeforeLoginProcessable {
    companion object : BufferReadable<LoginByUidC2SPacket> {
        override fun read(buf: FriendlyByteBuf) = LoginByUidC2SPacket(ObjectId(buf.readUtf()), buf.readUtf())
    }

    override suspend fun process(clientAddress: InetSocketAddress) = ConePlayer.loginByUid(clientAddress,this)


}