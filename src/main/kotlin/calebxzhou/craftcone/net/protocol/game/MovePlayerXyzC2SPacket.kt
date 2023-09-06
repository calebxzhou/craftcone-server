package calebxzhou.craftcone.net.protocol.game

import calebxzhou.craftcone.net.ConeNetSender.sendPacket
import calebxzhou.craftcone.net.protocol.BufferReadable
import calebxzhou.craftcone.net.protocol.InRoomProcessable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.server.entity.ConeOnlinePlayer
import calebxzhou.craftcone.server.entity.ConeRoom
import io.netty.buffer.ByteBuf

/**
 * Created  on 2023-07-13,10:21.
 */
data class MovePlayerXyzC2SPacket(
    val x: Float,
    val y: Float,
    val z: Float,
) : Packet, InRoomProcessable {
    companion object: BufferReadable<MovePlayerXyzC2SPacket> {
        override fun read(buf: ByteBuf)=MovePlayerXyzC2SPacket(buf.readFloat(),buf.readFloat(),buf.readFloat())

    }
    override suspend fun process(player: ConeOnlinePlayer, playingRoom: ConeRoom) =
        player.sendPacket(PlayerMoveXyzS2CPacket(player.data.id,x,y,z))


}