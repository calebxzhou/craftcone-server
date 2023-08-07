package calebxzhou.craftcone.net.protocol.game

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.InRoomProcessable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.net.protocol.BufferReadable
import calebxzhou.craftcone.server.entity.Player
import calebxzhou.craftcone.server.entity.Room

/**
 * Created  on 2023-07-06,8:48.
 */
data class ChatC2CPacket (
    val senderName: String,
    val content: String,
): Packet, InRoomProcessable, BufferWritable {


    companion object : BufferReadable<ChatC2CPacket>{
        override fun read(buf: FriendlyByteBuf): ChatC2CPacket {
            return ChatC2CPacket(buf.readUtf(),buf.readUtf())
        }
    }

    override fun write(buf: FriendlyByteBuf) {
        buf.writeUtf(senderName)
        buf.writeUtf(content)
    }

    override fun process(player: Player, playingRoom: Room) {
        playingRoom.broadcastPacket(this)
    }


}