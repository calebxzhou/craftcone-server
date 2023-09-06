package calebxzhou.craftcone.net.protocol.room

import calebxzhou.craftcone.net.protocol.AfterLoginProcessable
import calebxzhou.craftcone.net.protocol.BufferReadable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.server.entity.ConeOnlinePlayer
import calebxzhou.craftcone.server.entity.ConeRoom
import io.netty.buffer.ByteBuf

/**
 * Created  on 2023-08-13,16:10.
 */
 class GetMyRoomC2SPacket(
): Packet,AfterLoginProcessable {
    companion object:BufferReadable<GetMyRoomC2SPacket>{
        override fun read(buf: ByteBuf): GetMyRoomC2SPacket {
            return GetMyRoomC2SPacket()
        }

    }
    override suspend fun process(player: ConeOnlinePlayer) {
        ConeRoom.onPlayerGetMy(player)
    }

}
