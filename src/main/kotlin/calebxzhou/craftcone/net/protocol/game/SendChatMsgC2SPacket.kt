package calebxzhou.craftcone.net.protocol.game

import calebxzhou.craftcone.net.ConeNetSender.sendPacketToAll
import calebxzhou.craftcone.net.protocol.*
import calebxzhou.craftcone.net.protocol.general.SysMsgS2CPacket
import calebxzhou.craftcone.server.entity.ConeOnlinePlayer
import calebxzhou.craftcone.server.entity.ConeRoom
import calebxzhou.craftcone.util.ByteBufUt.readUtf
import io.netty.buffer.ByteBuf

/**
 * Created  on 2023-07-06,8:48.
 */
data class SendChatMsgC2SPacket (
    val msg: String,
): Packet, InRoomProcessable {
    companion object : BufferReadable<SendChatMsgC2SPacket>{
        override fun read(buf: ByteBuf) = SendChatMsgC2SPacket(buf.readUtf())
    }

    override suspend fun process(player: ConeOnlinePlayer, playingRoom: ConeRoom) {
        playingRoom.sendPacketToAll(player,SysMsgS2CPacket(MsgType.Chat,MsgLevel.Normal,msg)  )
    }


}