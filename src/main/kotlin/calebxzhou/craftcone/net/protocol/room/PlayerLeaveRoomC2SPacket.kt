package calebxzhou.craftcone.net.protocol.room

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.BufferReadable
import calebxzhou.craftcone.net.protocol.InRoomProcessable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.server.entity.Player
import calebxzhou.craftcone.server.entity.Room

/**
 * Created  on 2023-07-06,8:48.
 */
//玩家离开房间
class PlayerLeaveRoomC2SPacket : Packet, InRoomProcessable {

    companion object : BufferReadable<PlayerLeaveRoomC2SPacket>{
        override fun read(buf: FriendlyByteBuf): PlayerLeaveRoomC2SPacket {
            return PlayerLeaveRoomC2SPacket()
        }
    }

    override fun process(player: Player, playingRoom: Room) {
        player.leaveRoom()
        playingRoom.broadcastPacket(PlayerLeftRoomS2CPacket(player.id),player)
    }

}