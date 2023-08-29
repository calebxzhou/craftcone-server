package calebxzhou.craftcone.net.protocol.game

import calebxzhou.craftcone.net.ConeNetSender.sendPacket
import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.BufferReadable
import calebxzhou.craftcone.net.protocol.InRoomProcessable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.server.entity.ConePlayer
import calebxzhou.craftcone.server.entity.ConeRoom

/**
 * Created  on 2023-07-13,10:21.
 */
data class MovePlayerWpC2SPacket(
    val w: Float,
    val p: Float,
) : Packet,InRoomProcessable    {
    companion object : BufferReadable<MovePlayerWpC2SPacket>{
        override fun read(buf: FriendlyByteBuf) = MovePlayerWpC2SPacket(buf.readFloat(),buf.readFloat())
    }

    override suspend fun process(player: ConePlayer, playingRoom: ConeRoom) {
        player.sendPacket(PlayerMoveWpS2CPacket(player.id,w,p))
    }


}