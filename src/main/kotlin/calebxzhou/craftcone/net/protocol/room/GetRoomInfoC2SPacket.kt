package calebxzhou.craftcone.net.protocol.room

import calebxzhou.craftcone.net.ConeNetSender
import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.AfterLoginProcessable
import calebxzhou.craftcone.net.protocol.BufferReadable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.server.entity.Player
import calebxzhou.craftcone.server.entity.Room
import calebxzhou.craftcone.server.logger

/**
 * Created  on 2023-08-13,16:10.
 */
data class GetRoomInfoC2SPacket(
    val rid:Int
): Packet,AfterLoginProcessable {
    companion object:BufferReadable<GetRoomInfoC2SPacket>{
        override fun read(buf: FriendlyByteBuf): GetRoomInfoC2SPacket {
            return GetRoomInfoC2SPacket(buf.readVarInt())
        }

    }
    override fun process(player: Player) {
        val room = Room.read(rid) ?:let{
            logger.info { "找不到房间$rid" }
            return
        }
        ConeNetSender.sendPacket(room,player)
    }

}
