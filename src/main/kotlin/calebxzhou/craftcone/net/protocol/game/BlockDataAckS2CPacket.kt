package calebxzhou.craftcone.net.protocol.game

import calebxzhou.craftcone.net.ConeByteBuf
import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.server.entity.ConeBlockPos

data class BlockDataAckS2CPacket(
    val dimId:Int,val blockPos: ConeBlockPos
): Packet,BufferWritable{
    override fun write(buf: ConeByteBuf) {
        buf.writeVarInt(dimId)
        buf.writeLong(blockPos.asLong)
    }

}
