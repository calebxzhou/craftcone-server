package calebxzhou.craftcone.net.protocol.room

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.C2SPacket
import java.net.InetSocketAddress
import java.util.*

/**
 * Created  on 2023-07-06,8:48.
 */
data class PlayerLeaveRoomC2SPacket (
    val pid: UUID,
    val pName: String,
): C2SPacket {

    companion object{
        fun read(buf: FriendlyByteBuf): PlayerLeaveRoomC2SPacket {
            return PlayerLeaveRoomC2SPacket(buf.readUUID(),buf.readUtf())
        }
    }

    override fun process(clientAddress: InetSocketAddress) {

    }


}