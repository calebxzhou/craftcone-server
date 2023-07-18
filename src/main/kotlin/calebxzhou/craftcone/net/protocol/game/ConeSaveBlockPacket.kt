package calebxzhou.craftcone.net.protocol.game

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.ConeInGamePacket

/**
 * Created  on 2023-07-17,17:16.
 */
//保存单个方块的包（setBlock）
data class ConeSaveBlockPacket(
    //维度
    val dimName: String,
    //方块位置
    val bpos: Long,
    //状态
    val state: String,
) : ConeInGamePacket{
    override fun process() {
    }

    override fun write(buf: FriendlyByteBuf) {
        buf.writeUtf(dimName)
        buf.writeLong(bpos )
        buf.writeUtf(state)
    }

}
