package calebxzhou.craftcone.net.protocol.room

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.C2SPacket
import calebxzhou.craftcone.net.protocol.ReadablePacket
import java.net.InetSocketAddress
import java.util.*

/**
 * Created  on 2023-07-06,8:48.
 */
data class PlayerJoinRoomC2SPacket (
    val pid: UUID,
    val pName: String,
): C2SPacket {

    companion object : ReadablePacket{
        override fun read(buf: FriendlyByteBuf): PlayerJoinRoomC2SPacket {
            return PlayerJoinRoomC2SPacket(buf.readUUID(),buf.readUtf())
        }
    }

    override fun process(clientAddress: InetSocketAddress) {

    }


}