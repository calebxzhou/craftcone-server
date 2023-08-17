package calebxzhou.craftcone.net.protocol.game

import calebxzhou.craftcone.net.ConeNetSender
import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.BufferReadable
import calebxzhou.craftcone.net.protocol.InRoomProcessable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.server.entity.ConeChunkPos
import calebxzhou.craftcone.server.entity.ConePlayer
import calebxzhou.craftcone.server.entity.ConeRoom

/**
 * Created  on 2023-07-17,17:16.
 */
//玩家请求获取方块
data class GetChunkC2SPacket(
    //维度ID
    val dimId: Int,
    //区块位置 long
    val chunkPos: ConeChunkPos,
) : Packet, InRoomProcessable {
    companion object : BufferReadable<GetChunkC2SPacket>{
        override fun read(buf: FriendlyByteBuf): GetChunkC2SPacket {
            return GetChunkC2SPacket(buf.readVarInt(),ConeChunkPos(buf.readLong()))
        }
    }


    override fun process(player: ConePlayer, playingRoom: ConeRoom) {
        playingRoom.readBlock(dimId,chunkPos){bpos,bsid,tag->
            ConeNetSender.sendPacket(BlockDataC2CPacket(dimId,bpos ,bsid,tag),player.addr)
        }


    }


}
