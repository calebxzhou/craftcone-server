package calebxzhou.craftcone.net.protocol.game

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.ConeInGamePacket
import java.util.UUID

/**
 * Created  on 2023-07-13,10:21.
 */
data class ConePlayerMovePacket(
    val pid: UUID,
    val x:Float,
    val y:Float,
    val z:Float,
) : ConeInGamePacket {
    override fun write(buf: FriendlyByteBuf) {
        buf.writeUUID(pid)
        buf.writeFloat(x)
        buf.writeFloat(y)
        buf.writeFloat(z)
    }

    override fun process() {
    }


}