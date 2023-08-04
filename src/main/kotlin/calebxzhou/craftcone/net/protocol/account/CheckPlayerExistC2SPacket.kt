package calebxzhou.craftcone.net.protocol.account

import calebxzhou.craftcone.net.ConeNetSender
import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.BeforeLoginProcessable
import calebxzhou.craftcone.net.protocol.BufferReadable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.server.entity.ConePlayer
import java.net.InetSocketAddress
import java.util.*

/**
 * Created  on 2023-07-18,7:41.
 */
//用户是否存在（已注册）
data class CheckPlayerExistC2SPacket(
    val pid: UUID
): Packet,BeforeLoginProcessable{
    companion object : BufferReadable<CheckPlayerExistC2SPacket>{
        override fun read(buf: FriendlyByteBuf): CheckPlayerExistC2SPacket {
            return CheckPlayerExistC2SPacket(buf.readUUID())
        }

    }

    override fun process(clientAddress: InetSocketAddress) {
        ConeNetSender.sendPacket(CheckPlayerExistS2CPacket(ConePlayer.isRegistered(pid)),clientAddress)
    }

}
