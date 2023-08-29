package calebxzhou.craftcone.net.protocol.game

import calebxzhou.craftcone.net.ConeByteBuf
import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.Packet
import org.bson.types.ObjectId

/**
 * Created  on 2023-07-13,10:21.
 */
data class PlayerMoveWpS2CPacket(
    val pid:ObjectId,
    val w:Float,
    val p:Float,
) : Packet,BufferWritable   {
    override fun write(buf: ConeByteBuf) {
        buf.writeObjectId(pid).writeFloat(w).writeFloat(p)
    }

}