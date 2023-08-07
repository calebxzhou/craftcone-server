package calebxzhou.craftcone.net.protocol.game

import calebxzhou.craftcone.net.ConeNetSender
import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.BufferReadable
import calebxzhou.craftcone.net.protocol.InRoomProcessable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.server.entity.ChunkPos
import calebxzhou.craftcone.server.entity.Player
import calebxzhou.craftcone.server.entity.Room

/**
 * Created  on 2023-07-17,17:16.
 */
//玩家请求获取方块
data class ReadBlockC2SPacket(
    //维度ID
    val dimId: Int,
    //区块位置 long
    val chunkPos: ChunkPos,
) : Packet, InRoomProcessable {
    companion object : BufferReadable<ReadBlockC2SPacket>{
        override fun read(buf: FriendlyByteBuf): ReadBlockC2SPacket {
            return ReadBlockC2SPacket(buf.readVarInt(),ChunkPos(buf.readLong()))
        }
    }


    override fun process(player: Player, playingRoom: Room) {
        playingRoom.readBlock(dimId,chunkPos){bpos,bsid->
            ConeNetSender.sendPacket(ReadBlockS2CPacket(dimId,bpos ,bsid),player.addr)
        }


    }


}
