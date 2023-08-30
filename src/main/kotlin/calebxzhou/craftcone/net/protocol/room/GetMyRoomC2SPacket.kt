package calebxzhou.craftcone.net.protocol.room

import calebxzhou.craftcone.net.ConeByteBuf
import calebxzhou.craftcone.net.protocol.AfterLoginProcessable
import calebxzhou.craftcone.net.protocol.BufferReadable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.server.entity.ConePlayer
import calebxzhou.craftcone.server.entity.ConeRoom

/**
 * Created  on 2023-08-13,16:10.
 */
 class GetMyRoomC2SPacket(
): Packet,AfterLoginProcessable {
    companion object:BufferReadable<GetMyRoomC2SPacket>{
        override fun read(buf: ConeByteBuf): GetMyRoomC2SPacket {
            return GetMyRoomC2SPacket()
        }

    }
    override suspend fun process(player: ConePlayer) {
        ConeRoom.onPlayerGetMy(player)
    }

}
