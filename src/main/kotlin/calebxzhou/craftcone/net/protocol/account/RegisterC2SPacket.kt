package calebxzhou.craftcone.net.protocol.account

import calebxzhou.craftcone.net.ConeNetManager
import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.C2SPacket
import calebxzhou.craftcone.net.protocol.ReadablePacket
import calebxzhou.craftcone.server.LOG
import calebxzhou.craftcone.server.model.ConePlayer
import calebxzhou.craftcone.server.model.ConePlayer.Companion.PWD_FILE
import java.net.InetSocketAddress
import java.nio.file.Files
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
        val pDir = ConePlayer.getProfilePath(pid)
        if(Files.exists(pDir)){
            ConeNetManager.sendPacket(RegisterS2CPacket(false,"已注册过"),clientAddress)
            return
        }

        Files.createDirectories(pDir)
        Files.writeString(pDir.resolve(PWD_FILE),pwd)
        LOG.info { "$pid 已注册" }
        ConeNetManager.sendPacket(RegisterS2CPacket(true,""),clientAddress)


    }

}
