package calebxzhou.craftcone.net.protocol.room

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.S2CPacket

/**
 * Created  on 2023-08-03,19:47.
 */
data class PlayerJoinRoomS2CPacket(
    val ok:Boolean,
    val data:String
):S2CPacket{
    override fun write(buf: FriendlyByteBuf) {
        buf.writeBoolean(ok)
        buf.writeUtf(data)
    }

}
