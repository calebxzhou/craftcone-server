package calebxzhou.craftcone.net.protocol.room

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.C2SPacket
import calebxzhou.craftcone.net.protocol.ReadablePacket
import java.net.InetSocketAddress

/**
 * Created  on 2023-07-06,8:48.
 */
//玩家请求创建房间
data class PlayerCreateRoomC2SPacket(
    //是否创造模式
    val isCreative: Boolean,
    //mod加载器？Fabric：Forge
    val isFabric: Boolean,
    //方块状态数量
    val blockStateAmount: Int,
): C2SPacket {

    companion object : ReadablePacket<PlayerCreateRoomC2SPacket>{
        override fun read(buf: FriendlyByteBuf): PlayerCreateRoomC2SPacket {
            return PlayerCreateRoomC2SPacket(buf.readBoolean(),buf.readBoolean(),buf.readVarInt())
        }
    }

    override fun process(clientAddress: InetSocketAddress) {
        //检查这个玩家是否达到最大房间创建数

        //写硬盘
    }


}