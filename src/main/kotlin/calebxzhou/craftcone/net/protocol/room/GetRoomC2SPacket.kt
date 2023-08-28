package calebxzhou.craftcone.net.protocol.room

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.AfterLoginProcessable
import calebxzhou.craftcone.net.protocol.BufferReadable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.server.entity.ConePlayer
import calebxzhou.craftcone.server.entity.ConeRoom

/**
 * Created  on 2023-08-13,16:10.
 */
data class GetRoomC2SPacket(
    val rid:Int
): Packet,AfterLoginProcessable {
    companion object:BufferReadable<GetRoomC2SPacket>{
        override fun read(buf: FriendlyByteBuf): GetRoomC2SPacket {
            return GetRoomC2SPacket(buf.readVarInt())
        }

    }
    override suspend fun process(player: ConePlayer) {
        ConeRoom.onRetrieve(player, rid)
    }

}
