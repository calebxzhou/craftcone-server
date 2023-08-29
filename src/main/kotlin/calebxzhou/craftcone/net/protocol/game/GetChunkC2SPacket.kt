package calebxzhou.craftcone.net.protocol.game

import calebxzhou.craftcone.net.ConeByteBuf
import calebxzhou.craftcone.net.ConeNetSender.sendPacket
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
    val dimId: Int,
    val chunkPos: ConeChunkPos,
) : Packet, InRoomProcessable {
    companion object : BufferReadable<GetChunkC2SPacket>{
        override fun read(buf: ConeByteBuf): GetChunkC2SPacket {
            return GetChunkC2SPacket(buf.readVarInt(),ConeChunkPos(buf.readShort().toInt(), buf.readShort().toInt()))
        }
    }


    override suspend fun process(player: ConePlayer, playingRoom: ConeRoom) {
        playingRoom.readBlock(dimId,chunkPos){
            player.sendPacket(it.dto(dimId))
        }
    }


}
