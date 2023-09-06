package calebxzhou.craftcone.net.protocol.game

import calebxzhou.craftcone.net.ConeNetSender.sendPacket
import calebxzhou.craftcone.net.protocol.BufferReadable
import calebxzhou.craftcone.net.protocol.InRoomProcessable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.server.entity.ConeBlockData
import calebxzhou.craftcone.server.entity.ConeChunkPos
import calebxzhou.craftcone.server.entity.ConeOnlinePlayer
import calebxzhou.craftcone.server.entity.ConeRoom
import calebxzhou.craftcone.util.ByteBufUt.readVarInt
import io.netty.buffer.ByteBuf

/**
 * Created  on 2023-07-17,17:16.
 */
//玩家请求获取方块
data class GetChunkC2SPacket(
    val dimId: Int,
    val chunkPos: ConeChunkPos,
) : Packet, InRoomProcessable {
    companion object : BufferReadable<GetChunkC2SPacket>{
        override fun read(buf: ByteBuf): GetChunkC2SPacket {
            return GetChunkC2SPacket(buf.readVarInt(),ConeChunkPos(buf.readShort().toInt(), buf.readShort().toInt()))
        }
    }


    override suspend fun process(player: ConeOnlinePlayer, playingRoom: ConeRoom) {
        ConeBlockData.read(playingRoom.id,dimId,chunkPos.asInt){
            player.sendPacket(it.dto)
        }
    }


}
