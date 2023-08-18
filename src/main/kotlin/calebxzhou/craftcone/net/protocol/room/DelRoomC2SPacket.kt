package calebxzhou.craftcone.net.protocol.room

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.AfterLoginProcessable
import calebxzhou.craftcone.net.protocol.BufferReadable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.server.entity.ConePlayer
import calebxzhou.craftcone.server.entity.ConeRoom

/**
 * Created  on 2023-08-11,20:39.
 */
data class DelRoomC2SPacket(
    val rid:Int
): Packet,AfterLoginProcessable{
    companion object : BufferReadable<DelRoomC2SPacket>{
        override fun read(buf: FriendlyByteBuf): DelRoomC2SPacket {
            return DelRoomC2SPacket(buf.readVarInt())
        }

    }
    override fun process(player: ConePlayer) {
        ConeRoom.onDelete(player, rid)
    }

}
