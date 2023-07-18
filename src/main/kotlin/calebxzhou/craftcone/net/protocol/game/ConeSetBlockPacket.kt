package calebxzhou.craftcone.net.protocol.game

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.ConeInGamePacket

/**
 * Created  on 2023-06-29,20:46.
 */
//c2c 设置单个方块的包（玩家破坏+放置）
data class ConeSetBlockPacket(
    //维度ID
    val levelId: Int,
    //方块位置
    val bpos: Long,
    //状态
    val state: Int,
) : ConeInGamePacket {


    companion object {

        //从buf读
        fun read(buf: FriendlyByteBuf): ConeSetBlockPacket {
            return ConeSetBlockPacket(
                buf.readByte().toInt(),
                buf.readLong(),
                buf.readVarInt(),
            )
        }
    }

    override fun write(buf: FriendlyByteBuf) {
        buf.writeByte(levelId)
        buf.writeLong(bpos)
        buf.writeVarInt(state)
    }

    override fun process() {


    }


}
