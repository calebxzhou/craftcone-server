package calebxzhou.craftcone.net.protocol.general

import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.Packet
import io.netty.buffer.ByteBuf

/**
 * Created  on 2023-08-15,8:53.
 */
//断开与玩家的连接
class DisconnectS2CPacket:Packet,BufferWritable {
    override fun write(buf: ByteBuf) {
    }
}