package calebxzhou.craftcone.net.protocol.game

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.BufferReadable
import calebxzhou.craftcone.net.protocol.InRoomProcessable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.server.entity.BlockPos
import calebxzhou.craftcone.server.entity.ConePlayer
import calebxzhou.craftcone.server.entity.ConeRoom

/**
 * Created  on 2023-07-17,17:16.
 */
//玩家请求保存单个方块
data class WriteBlockC2SPacket(
    //维度ID
    val dimId: Int,
    //方块位置
    val bpos: BlockPos,
    //状态ID
    val stateId: Int,
) : Packet, InRoomProcessable{
    //区块位置
    val cpos = bpos.chunkPos
    //第几个section
    val sectionIndex = bpos.y ushr 4
    companion object : BufferReadable<WriteBlockC2SPacket>{

        override fun read(buf: FriendlyByteBuf): WriteBlockC2SPacket {
            return WriteBlockC2SPacket(
                buf.readVarInt(),
                BlockPos(buf.readLong()),
                buf.readVarInt())
        }
    }


    override fun process(player: ConePlayer, playingRoom: ConeRoom) {
        playingRoom.saveBlock(this)
    }

}
