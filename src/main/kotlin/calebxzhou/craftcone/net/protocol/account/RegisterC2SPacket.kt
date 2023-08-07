package calebxzhou.craftcone.net.protocol.account

import calebxzhou.craftcone.net.ConeNetSender
import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.BeforeLoginProcessable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.net.protocol.BufferReadable
import calebxzhou.craftcone.server.entity.Player
import java.net.InetSocketAddress
import java.util.*

/**
 * Created  on 2023-07-21,10:37.
 */
data class RegisterC2SPacket(
    val pid: UUID,
    val pName:String,
    val pwd : String,
): Packet, BeforeLoginProcessable {
    companion object : BufferReadable<RegisterC2SPacket> {
        override fun read(buf: FriendlyByteBuf): RegisterC2SPacket {
            return RegisterC2SPacket(buf.readUUID(),buf.readUtf(),buf.readUtf())
        }

    }

    override fun process(clientAddress: InetSocketAddress) {
        val packet = if(Player.create(pid,pwd,pName))
            RegisterS2CPacket(true,"")
        else
            RegisterS2CPacket(false,"已注册了")
        ConeNetSender.sendPacket(packet,clientAddress)

    }

}
