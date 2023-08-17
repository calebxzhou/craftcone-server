package calebxzhou.craftcone.net.protocol.general

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.Packet

/**
 * Created  on 2023-08-15,8:53.
 */
class DisconnectS2CPacket:Packet,BufferWritable {
    override fun write(buf: FriendlyByteBuf) {
    }
}