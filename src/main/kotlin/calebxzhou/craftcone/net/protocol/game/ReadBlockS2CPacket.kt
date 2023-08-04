package calebxzhou.craftcone.net.protocol.game

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.Packet

/**
 * Created  on 2023-07-17,17:16.
 */
//请求方块响应
data class ReadBlockS2CPacket(
    //维度ID
    val dimId: Int,
    //方块位置
    val bpos: Long,
    //状态ID
    val state: Int,
) : Packet, BufferWritable {

    override fun write(buf: FriendlyByteBuf) {
        buf.writeVarInt(dimId)
        buf.writeLong(bpos)
        buf.writeVarInt(state)
    }


}
