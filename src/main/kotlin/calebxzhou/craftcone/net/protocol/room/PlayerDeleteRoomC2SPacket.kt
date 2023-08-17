package calebxzhou.craftcone.net.protocol.room

import calebxzhou.craftcone.net.ConeNetSender
import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.AfterLoginProcessable
import calebxzhou.craftcone.net.protocol.BufferReadable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.server.entity.ConePlayer
import calebxzhou.craftcone.server.entity.ConeRoom

/**
 * Created  on 2023-08-11,20:39.
 */
data class PlayerDeleteRoomC2SPacket(
    val rid:Int
): Packet,AfterLoginProcessable{
    companion object : BufferReadable<PlayerDeleteRoomC2SPacket>{
        override fun read(buf: FriendlyByteBuf): PlayerDeleteRoomC2SPacket {
            return PlayerDeleteRoomC2SPacket(buf.readVarInt())
        }

    }
    override fun process(player: ConePlayer) {
        val ownerId = ConeRoom.getOwnerId(rid)
        if(ownerId < 0 || ownerId != player.id){
            ConeNetSender.sendPacket(PlayerDeleteRoomS2CPacket(false,"没有房间$rid"),player)
        }
        if(ConeRoom.delete(rid)){
            ConeNetSender.sendPacket(PlayerDeleteRoomS2CPacket(true,""),player)
        }

    }

}
