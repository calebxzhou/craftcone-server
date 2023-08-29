package calebxzhou.craftcone.net.protocol.game

import calebxzhou.craftcone.net.ConeNetSender.sendPacketToAll
import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.*
import calebxzhou.craftcone.net.protocol.general.SysMsgS2CPacket
import calebxzhou.craftcone.server.entity.ConePlayer
import calebxzhou.craftcone.server.entity.ConeRoom

/**
 * Created  on 2023-07-06,8:48.
 */
data class SendChatMsgC2SPacket (
    val msg: String,
): Packet, InRoomProcessable {
    companion object : BufferReadable<SendChatMsgC2SPacket>{
        override fun read(buf: FriendlyByteBuf) = SendChatMsgC2SPacket(buf.readUtf())
    }

    override suspend fun process(player: ConePlayer, playingRoom: ConeRoom) {
        playingRoom.sendPacketToAll(player,SysMsgS2CPacket(MsgType.Chat,MsgLevel.Normal,msg)  )
    }


}