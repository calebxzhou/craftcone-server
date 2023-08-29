package calebxzhou.craftcone.net.protocol.room

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.AfterLoginProcessable
import calebxzhou.craftcone.net.protocol.BufferReadable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.server.entity.ConePlayer
import calebxzhou.craftcone.server.entity.ConeRoom
import org.bson.types.ObjectId

/**
 * Created  on 2023-07-06,8:48.
 */
//玩家请求加入房间
data class JoinRoomC2SPacket(
    val rid: ObjectId
): Packet, AfterLoginProcessable {
    companion object : BufferReadable<JoinRoomC2SPacket>{
        override fun read(buf: FriendlyByteBuf) = JoinRoomC2SPacket(buf.readObjectId())

    }


    override suspend fun process(player: ConePlayer) = ConeRoom.onPlayerJoin(player,rid)


}