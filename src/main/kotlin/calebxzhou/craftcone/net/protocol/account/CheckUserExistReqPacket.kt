package calebxzhou.craftcone.net.protocol.account

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.ConeOutGamePacket
import calebxzhou.craftcone.net.protocol.ReadablePacket
import java.net.InetSocketAddress
import java.util.*

/**
 * Created  on 2023-07-18,7:41.
 */
//用户是否存在（已注册）
data class CheckUserExistReqPacket(
    val uid: UUID
): ConeOutGamePacket{
    companion object : ReadablePacket{
        override fun read(buf: FriendlyByteBuf): CheckUserExistReqPacket {
            //for server
            return CheckUserExistReqPacket(buf.readUUID())
        }

    }

    override fun process(clientAddress: InetSocketAddress) {
        //for server

    }

    override fun write(buf: FriendlyByteBuf) {
        buf.writeUUID(uid)
    }

}
