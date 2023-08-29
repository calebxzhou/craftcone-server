package calebxzhou.craftcone.net.protocol.room

import calebxzhou.craftcone.net.ConeByteBuf
import calebxzhou.craftcone.net.protocol.AfterLoginProcessable
import calebxzhou.craftcone.net.protocol.BufferReadable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.server.entity.ConePlayer
import calebxzhou.craftcone.server.entity.ConeRoom
import org.bson.types.ObjectId

/**
 * Created  on 2023-08-13,16:10.
 */
data class GetRoomC2SPacket(
    val rid:ObjectId
): Packet,AfterLoginProcessable {
    companion object:BufferReadable<GetRoomC2SPacket>{
        override fun read(buf: ConeByteBuf): GetRoomC2SPacket {
            return GetRoomC2SPacket(buf.readObjectId())
        }

    }
    override suspend fun process(player: ConePlayer) {
        ConeRoom.onPlayerGet(player, rid)
    }

}
