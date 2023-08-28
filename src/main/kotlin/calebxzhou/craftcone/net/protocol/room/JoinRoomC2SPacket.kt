package calebxzhou.craftcone.net.protocol.room

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.AfterLoginProcessable
import calebxzhou.craftcone.net.protocol.BufferReadable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.server.entity.ConePlayer
import calebxzhou.craftcone.server.entity.ConeRoom

/**
 * Created  on 2023-07-06,8:48.
 */
//玩家请求加入房间
data class JoinRoomC2SPacket(
    val rid: Int
): Packet, AfterLoginProcessable {
    companion object : BufferReadable<JoinRoomC2SPacket>{
        override fun read(buf: FriendlyByteBuf): JoinRoomC2SPacket {
            return JoinRoomC2SPacket(buf.readVarInt())
        }

    }


    override suspend fun process(player: ConePlayer){
        ConeRoom.onPlayerJoin(player,rid)
    }


}