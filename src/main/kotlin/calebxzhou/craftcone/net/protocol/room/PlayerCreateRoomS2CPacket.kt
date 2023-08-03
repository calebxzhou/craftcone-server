package calebxzhou.craftcone.net.protocol.room

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.S2CPacket

/**
 * Created  on 2023-08-01,19:18.
 */
data class PlayerCreateRoomS2CPacket(
    val isSuccess:Boolean,
    val data: String,
) : S2CPacket{
    override fun write(buf: FriendlyByteBuf) {
        buf.writeBoolean(isSuccess)
        buf.writeUtf(data)
    }

}
