package calebxzhou.craftcone.net.protocol.account

import calebxzhou.craftcone.net.ConeNetManager
import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.C2SPacket
import calebxzhou.craftcone.net.protocol.ReadablePacket
import calebxzhou.craftcone.server.model.ConePlayer
import java.net.InetSocketAddress
import java.nio.file.Files
import java.util.*

/**
 * Created  on 2023-07-18,7:41.
 */
//用户是否存在（已注册）
data class CheckPlayerExistC2SPacket(
    val pid: UUID
): C2SPacket{
    companion object : ReadablePacket{
        override fun read(buf: FriendlyByteBuf): CheckPlayerExistC2SPacket {
            return CheckPlayerExistC2SPacket(buf.readUUID())
        }

    }

    override fun process(clientAddress: InetSocketAddress) {
        val exists = Files.exists(ConePlayer.getProfilePath(pid))
        ConeNetManager.sendPacket(CheckPlayerExistS2CPacket(exists),clientAddress)

    }

}
