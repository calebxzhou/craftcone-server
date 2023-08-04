package calebxzhou.craftcone.net.protocol.game

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.InRoomProcessable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.net.protocol.BufferReadable
import calebxzhou.craftcone.server.entity.ConePlayer
import calebxzhou.craftcone.server.entity.ConeRoom

/**
 * Created  on 2023-07-17,17:16.
 */
//玩家请求保存单个方块
data class SaveBlockC2SPacket(
    //维度ID
    val dimId: Int,
    //方块位置
    val bpos: Long,
    //状态ID
    val stateId: Int,
) : Packet, InRoomProcessable{
    companion object : BufferReadable<SaveBlockC2SPacket>{
        override fun read(buf: FriendlyByteBuf): SaveBlockC2SPacket {
            return SaveBlockC2SPacket(buf.readVarInt(),buf.readLong(),buf.readVarInt())
        }

    }

    override fun process(player: ConePlayer, playingRoom: ConeRoom) {
        playingRoom.saveBlock(dimId, bpos, stateId)
    }

}
