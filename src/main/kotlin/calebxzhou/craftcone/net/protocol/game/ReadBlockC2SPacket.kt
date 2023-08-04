package calebxzhou.craftcone.net.protocol.game

import calebxzhou.craftcone.net.ConeNetManager
import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.InRoomProcessable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.net.protocol.BufferReadable
import calebxzhou.craftcone.server.entity.ConePlayer
import calebxzhou.craftcone.server.entity.ConeRoom

/**
 * Created  on 2023-07-17,17:16.
 */
//玩家请求获取方块
data class ReadBlockC2SPacket(
    //维度ID
    val dimId: Int,
    //方块位置
    val bpos: Long,
) : Packet, InRoomProcessable {
    companion object : BufferReadable<ReadBlockC2SPacket>{
        override fun read(buf: FriendlyByteBuf): ReadBlockC2SPacket {
            return ReadBlockC2SPacket(buf.readVarInt(),buf.readLong())
        }

    }


    override fun process(player: ConePlayer, playingRoom: ConeRoom) {
        ConeNetManager.sendPacket(ReadBlockS2CPacket(dimId,bpos,playingRoom.readBlock(dimId,bpos)),player.addr)
    }


}
