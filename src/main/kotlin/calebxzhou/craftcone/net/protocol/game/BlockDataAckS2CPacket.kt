package calebxzhou.craftcone.net.protocol.game

import calebxzhou.craftcone.net.ConeByteBuf
import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.Packet

data class BlockDataAckS2CPacket(
    val dimId:Int,val blockPosl: Long
): Packet,BufferWritable{
    override fun write(buf: ConeByteBuf) {
        buf.writeVarInt(dimId)
        buf.writeLong(blockPosl)
    }

}
