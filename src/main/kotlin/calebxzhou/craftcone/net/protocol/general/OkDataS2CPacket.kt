package calebxzhou.craftcone.net.protocol.general

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.Packet

/**
 * Created  on 2023-08-15,8:47.
 */
//处理成功&数据
data class OkDataS2CPacket(val dataWriter: ((FriendlyByteBuf) -> Unit)? = null): Packet,BufferWritable {

    override fun write(buf: FriendlyByteBuf) {
        dataWriter?.let { it(buf) }
    }
}