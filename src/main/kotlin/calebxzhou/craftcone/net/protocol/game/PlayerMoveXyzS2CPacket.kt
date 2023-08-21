package calebxzhou.craftcone.net.protocol.game

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.Packet

/**
 * Created  on 2023-07-13,10:21.
 */
data class PlayerMoveXyzS2CPacket(
    val pid:Int,
    val x:Float,
    val y:Float,
    val z:Float,
) : Packet, BufferWritable {
    override fun write(buf: FriendlyByteBuf) {
        buf.writeVarInt(pid).writeFloat(x).writeFloat(y).writeFloat(z)
    }

}