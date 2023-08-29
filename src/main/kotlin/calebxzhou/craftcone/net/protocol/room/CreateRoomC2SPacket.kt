package calebxzhou.craftcone.net.protocol.room

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.AfterLoginProcessable
import calebxzhou.craftcone.net.protocol.BufferReadable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.server.entity.ConePlayer
import calebxzhou.craftcone.server.entity.ConeRoom

/**
 * Created  on 2023-07-06,8:48.
 */
//玩家请求创建房间
data class CreateRoomC2SPacket(
    //房间名称
    val rName:String,
    //mc版本
    val mcVersion : String,
    //是否创造模式
    val isCreative: Boolean,
    //方块状态数量
    val blockStateAmount: Int,
): Packet, AfterLoginProcessable {

    companion object : BufferReadable<CreateRoomC2SPacket>{
        override fun read(buf: FriendlyByteBuf): CreateRoomC2SPacket = CreateRoomC2SPacket(
            buf.readUtf(),
            buf.readUtf(), buf.readBoolean(),buf.readVarInt())
    }

    override suspend fun process(player: ConePlayer) {
        ConeRoom.onPlayerCreate(player, this)
    }


}