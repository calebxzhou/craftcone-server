package calebxzhou.craftcone.net.protocol.account

import calebxzhou.craftcone.net.ConeNetManager
import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.C2SPacket
import calebxzhou.craftcone.net.protocol.ReadablePacket
import calebxzhou.craftcone.server.PlayerManager
import calebxzhou.craftcone.server.entity.ConePlayer
import java.net.InetSocketAddress
import java.util.*

/**
 * Created  on 2023-07-21,10:37.
 */
data class RegisterC2SPacket(
    val pid: UUID,
    val pwd : String,
): C2SPacket {
    companion object : ReadablePacket<RegisterC2SPacket> {
        override fun read(buf: FriendlyByteBuf): RegisterC2SPacket {
            return RegisterC2SPacket(buf.readUUID(),buf.readUtf())
        }

    }

    override fun process(clientAddress: InetSocketAddress) {
        val packet = if(PlayerManager.register(ConePlayer(pid,pwd,clientAddress)))
            RegisterS2CPacket(true,"")
        else
            RegisterS2CPacket(false,"已注册了")
        ConeNetManager.sendPacket(packet,clientAddress)

    }

}
