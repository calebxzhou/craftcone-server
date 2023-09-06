package calebxzhou.craftcone.net.protocol.room

import calebxzhou.craftcone.net.protocol.AfterLoginProcessable
import calebxzhou.craftcone.net.protocol.BufferReadable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.server.entity.ConeOnlinePlayer
import calebxzhou.craftcone.server.entity.ConeRoom
import calebxzhou.craftcone.util.ByteBufUt.readObjectId
import io.netty.buffer.ByteBuf
import org.bson.types.ObjectId

/**
 * Created  on 2023-08-13,16:10.
 */
data class GetRoomC2SPacket(
    val rid:ObjectId
): Packet,AfterLoginProcessable {
    companion object:BufferReadable<GetRoomC2SPacket>{
        override fun read(buf: ByteBuf): GetRoomC2SPacket {
            return GetRoomC2SPacket(buf.readObjectId())
        }

    }
    override suspend fun process(player: ConeOnlinePlayer) {
        ConeRoom.onPlayerGet(player, rid)
    }

}
