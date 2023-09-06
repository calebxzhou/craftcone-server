package calebxzhou.craftcone.net.protocol.game

import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.util.ByteBufUt.writeObjectId
import io.netty.buffer.ByteBuf
import org.bson.types.ObjectId

/**
 * Created  on 2023-07-13,10:21.
 */
data class PlayerMoveXyzS2CPacket(
    val pid: ObjectId,
    val x:Float,
    val y:Float,
    val z:Float,
) : Packet, BufferWritable {
    override fun write(buf: ByteBuf) {
        buf.writeObjectId(pid).writeFloat(x).writeFloat(y).writeFloat(z)
    }

}