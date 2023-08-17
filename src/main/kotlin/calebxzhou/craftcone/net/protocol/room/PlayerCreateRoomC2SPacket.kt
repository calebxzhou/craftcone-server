package calebxzhou.craftcone.net.protocol.room

import calebxzhou.craftcone.net.ConeNetSender
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
data class PlayerCreateRoomC2SPacket(
    //房间名称
    val rName:String,
    //mc版本
    val mcVersion : String,
    //是否创造模式
    val isCreative: Boolean,
    //mod加载器？Fabric：Forge
    val isFabric: Boolean,
    //方块状态数量
    val blockStateAmount: Int,
): Packet, AfterLoginProcessable {

    companion object : BufferReadable<PlayerCreateRoomC2SPacket>{
        override fun read(buf: FriendlyByteBuf): PlayerCreateRoomC2SPacket {
            return PlayerCreateRoomC2SPacket(
                buf.readUtf(),
                buf.readUtf(),
                buf.readBoolean(),buf.readBoolean(),buf.readVarInt())
        }
    }

    override fun process(player: ConePlayer) {
        if(player.ownRoomAmount>5)
            return
        //创建
        val rid = ConeRoom.create(player, rName,mcVersion, isCreative, isFabric, blockStateAmount)
        ConeNetSender.sendPacket(PlayerCreateRoomS2CPacket(true,rid.toString()),player)
    }


}