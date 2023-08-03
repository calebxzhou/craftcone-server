package calebxzhou.craftcone.net.protocol.game

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.C2SPacket
import calebxzhou.craftcone.net.protocol.ReadablePacket
import java.net.InetSocketAddress

/**
 * Created  on 2023-07-17,17:16.
 */
//保存单个方块的包（setBlock）
data class SaveBlockC2SPacket(
    //维度ID
    val dimId: Int,
    //方块位置
    val bpos: Long,
    //状态ID
    val state: Int,
) : C2SPacket{
    companion object : ReadablePacket<SaveBlockC2SPacket>{
        override fun read(buf: FriendlyByteBuf): SaveBlockC2SPacket {
            return SaveBlockC2SPacket(buf.readVarInt(),buf.readLong(),buf.readVarInt())
        }

    }

    override fun process(clientAddress: InetSocketAddress) {
        //方块数据写硬盘
    }


}
