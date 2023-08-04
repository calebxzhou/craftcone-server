package calebxzhou.craftcone.net.protocol.game

import calebxzhou.craftcone.net.ConeNetManager
import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.C2SPacket
import calebxzhou.craftcone.net.protocol.ReadablePacket
import calebxzhou.craftcone.server.entity.ConeRoom
import calebxzhou.craftcone.server.logger
import java.net.InetSocketAddress

/**
 * Created  on 2023-07-17,17:16.
 */
data class ReadBlockC2SPacket(
    //维度ID
    val dimId: Int,
    //方块位置
    val bpos: Long,
) : C2SPacket{
    companion object : ReadablePacket<ReadBlockC2SPacket>{
        override fun read(buf: FriendlyByteBuf): ReadBlockC2SPacket {
            return ReadBlockC2SPacket(buf.readVarInt(),buf.readLong())
        }

    }

    override fun process(clientAddress: InetSocketAddress) {
        val room  = ConeRoom.getPlayingRoomByAddr(clientAddress)?:let {
            logger.warn { "$clientAddress 未加入任何房间" }
            return
        }
        ConeNetManager.sendPacket(ReadBlockS2CPacket(dimId,bpos,room.readBlock(dimId,bpos)),clientAddress)
    }


}
