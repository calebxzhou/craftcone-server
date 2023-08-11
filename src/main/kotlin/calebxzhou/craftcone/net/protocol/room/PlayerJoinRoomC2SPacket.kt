package calebxzhou.craftcone.net.protocol.room

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.AfterLoginProcessable
import calebxzhou.craftcone.net.protocol.BufferReadable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.server.entity.Player

/**
 * Created  on 2023-07-06,8:48.
 */
//玩家请求加入房间
data class PlayerJoinRoomC2SPacket(
    val rid: Int
): Packet, AfterLoginProcessable {
    companion object : BufferReadable<PlayerJoinRoomC2SPacket>{
        override fun read(buf: FriendlyByteBuf): PlayerJoinRoomC2SPacket {
            return PlayerJoinRoomC2SPacket(buf.readVarInt())
        }

    }


    override fun process(player: Player) {
        player.joinRoom(rid)
    }


}