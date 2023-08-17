package calebxzhou.craftcone.net.protocol.game

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.BufferReadable
import calebxzhou.craftcone.net.protocol.InRoomProcessable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.server.entity.ConePlayer
import calebxzhou.craftcone.server.entity.ConeRoom

/**
 * Created  on 2023-07-13,10:21.
 */
data class MovePlayerXyzC2SPacket(
    val x: Float,
    val y: Float,
    val z: Float,
) : Packet, InRoomProcessable {
    companion object: BufferReadable<MovePlayerXyzC2SPacket> {
        override fun read(buf: FriendlyByteBuf)=MovePlayerXyzC2SPacket(buf.readFloat(),buf.readFloat(),buf.readFloat())

    }
    override fun process(player: ConePlayer, playingRoom: ConeRoom) {
        player.sendPacket(PlayerMoveXyzS2CPacket(player.id,x,y,z))
    }


}