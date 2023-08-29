package calebxzhou.craftcone.net.protocol.game

import calebxzhou.craftcone.net.ConeByteBuf
import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.Packet
import org.bson.types.ObjectId

/**
 * Created  on 2023-07-06,8:48.
 */
//有玩家加入了房间
data class PlayerJoinedRoomS2CPacket(
    val pid: ObjectId,
    val pName: String
) : Packet, BufferWritable{
    override fun write(buf: ConeByteBuf) {
        buf.writeObjectId(pid).writeUtf(pName)
    }
}