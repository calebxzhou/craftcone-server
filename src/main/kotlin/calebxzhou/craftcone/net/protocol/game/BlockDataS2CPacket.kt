package calebxzhou.craftcone.net.protocol.game

import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.util.ByteBufUt.writeUtf
import calebxzhou.craftcone.util.ByteBufUt.writeVarInt
import io.netty.buffer.ByteBuf

/**
 * Created  on 2023-07-17,17:16.
 */
//请求方块响应
data class BlockDataS2CPacket(
    //维度ID
    val dimId: Int,
    //方块位置
    val bposl: Long,
    //状态ID
    val stateId: Int,
    //nbt
    val tag: String?
) : Packet, BufferWritable {

    override fun write(buf: ByteBuf) {
        buf.writeVarInt(dimId)
        buf.writeLong(bposl)
        buf.writeVarInt(stateId)
        buf.writeUtf(tag?:"")
    }

}
