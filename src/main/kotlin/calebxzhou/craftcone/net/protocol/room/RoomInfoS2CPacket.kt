package calebxzhou.craftcone.net.protocol.room

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.Packet
import java.util.*

/**
 * Created  on 2023-08-02,9:58.
 */
data class RoomInfoS2CPacket(
    //房间ID
    val rid: UUID,
    //房间名
    val rName: String,
    //mod加载器？Fabric：Forge
    val isFabric: Boolean,
    //创造
    val isCreative: Boolean,
    //方块状态数量
    val blockStateAmount: Int,
    //地图种子
    val seed: Long,
): Packet, BufferWritable{
    override fun write(buf: FriendlyByteBuf) {
        buf.writeUUID(rid)
        buf.writeUtf(rName)
        buf.writeBoolean(isFabric)
        buf.writeBoolean(isCreative)
        buf.writeVarInt(blockStateAmount)
        buf.writeLong(seed)
    }


}