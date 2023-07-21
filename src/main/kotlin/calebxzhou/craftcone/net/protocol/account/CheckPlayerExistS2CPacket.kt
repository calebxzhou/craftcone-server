package calebxzhou.craftcone.net.protocol.account

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.S2CPacket

/**
 * Created  on 2023-07-18,7:46.
 */
data class CheckPlayerExistS2CPacket(
    val exists: Boolean
): S2CPacket {

    override fun write(buf: FriendlyByteBuf) {
        //server
        buf.writeBoolean(exists)
    }

}
