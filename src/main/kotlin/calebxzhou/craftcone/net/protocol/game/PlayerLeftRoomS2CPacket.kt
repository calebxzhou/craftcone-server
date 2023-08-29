package calebxzhou.craftcone.net.protocol.game

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.Packet
import org.bson.types.ObjectId

/**
 * Created  on 2023-08-11,11:50.
 */
//有玩家离开了房间
data class PlayerLeftRoomS2CPacket(
    val pid: ObjectId
): Packet,BufferWritable {
    override fun write(buf: FriendlyByteBuf) {
        buf.writeObjectId(pid)
    }

}
