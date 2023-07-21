package calebxzhou.craftcone.net.protocol.room

import calebxzhou.craftcone.net.protocol.C2SPacket
import java.net.InetSocketAddress

/**
 * Created  on 2023-07-17,17:16.
 */
//保存单个方块的包（setBlock）
data class SaveBlockC2SPacket(
    //维度
    val dimName: String,
    //方块位置
    val bpos: Long,
    //状态
    val state: String,
) : C2SPacket{

    override fun process(clientAddress: InetSocketAddress) {

    }


}
