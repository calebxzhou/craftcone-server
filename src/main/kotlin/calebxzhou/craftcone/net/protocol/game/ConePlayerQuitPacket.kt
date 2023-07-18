package calebxzhou.craftcone.net.protocol.game

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.ConeInGamePacket
import java.util.*

/**
 * Created  on 2023-07-06,8:48.
 */
data class ConePlayerQuitPacket (
    val pid: UUID,
    val pName: String,
): ConeInGamePacket {

    companion object{
        fun read(buf: FriendlyByteBuf): ConePlayerQuitPacket {
            return ConePlayerQuitPacket(buf.readUUID(),buf.readUtf())
        }
    }
    override fun write(buf: FriendlyByteBuf) {
        buf.writeUUID(pid)
        buf.writeUtf(pName)
    }

    override fun process() {
    }


}