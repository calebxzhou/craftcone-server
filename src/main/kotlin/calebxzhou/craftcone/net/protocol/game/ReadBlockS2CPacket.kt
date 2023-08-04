package calebxzhou.craftcone.net.protocol.game

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.S2CPacket

/**
 * Created  on 2023-07-17,17:16.
 */
data class ReadBlockS2CPacket(
    //维度ID
    val dimId: Int,
    //方块位置
    val bpos: Long,
    //状态ID
    val state: Int,
) : S2CPacket{


    override fun write(buf: FriendlyByteBuf) {
        buf.writeVarInt(dimId)
        buf.writeLong(bpos)
        buf.writeVarInt(state)
    }


}
