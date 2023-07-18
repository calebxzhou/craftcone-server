package calebxzhou.craftcone.net.protocol.account

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.ConeOutGamePacket
import calebxzhou.craftcone.net.protocol.ReadablePacket

/**
 * Created  on 2023-07-18,7:46.
 */
data class CheckUserExistRespPacket(
    val exists: Boolean
): ConeOutGamePacket{
    companion object : ReadablePacket{
        override fun read(buf: FriendlyByteBuf): Any {
            //client
            return CheckUserExistRespPacket(buf.readBoolean())
        }

    }
    override fun process() {
        //client
    }

    override fun write(buf: FriendlyByteBuf) {
        //server
        buf.writeBoolean(exists)
    }

}
