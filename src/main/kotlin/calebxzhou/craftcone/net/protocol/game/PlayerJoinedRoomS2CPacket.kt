package calebxzhou.craftcone.net.protocol.game

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.Packet

/**
 * Created  on 2023-07-06,8:48.
 */
//有玩家加入了房间
data class PlayerJoinedRoomS2CPacket(
    val pid: Int,
    val pName: String
) : Packet, BufferWritable{

    override fun write(buf: FriendlyByteBuf) {
        buf.writeVarInt(pid).writeUtf(pName)
    }
}