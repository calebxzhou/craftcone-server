package calebxzhou.craftcone.net.protocol.game

import calebxzhou.craftcone.net.ConeNetSender.sendPacketToAll
import calebxzhou.craftcone.net.protocol.BufferReadable
import calebxzhou.craftcone.net.protocol.InRoomProcessable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.server.entity.ConeOnlinePlayer
import calebxzhou.craftcone.server.entity.ConeRoom
import io.netty.buffer.ByteBuf

/**
 * Created  on 2023-07-13,10:21.
 */
data class MovePlayerWpC2SPacket(
    val w: Float,
    val p: Float,
) : Packet,InRoomProcessable    {
    companion object : BufferReadable<MovePlayerWpC2SPacket>{
        override fun read(buf: ByteBuf) = MovePlayerWpC2SPacket(buf.readFloat(),buf.readFloat())
    }

    override suspend fun process(player: ConeOnlinePlayer, playingRoom: ConeRoom) {
        playingRoom.sendPacketToAll(player,PlayerMoveWpS2CPacket(player.data.id,w,p))
    }


}