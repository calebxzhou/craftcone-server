package calebxzhou.craftcone.net.protocol.game

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.C2CPacket
import calebxzhou.craftcone.net.protocol.ReadablePacket
import java.net.InetSocketAddress

/**
 * Created  on 2023-06-29,20:46.
 */
//c2c 设置单个方块的包（玩家破坏+放置）
data class SetBlockC2CPacket(
    //维度ID
    val levelId: Int,
    //方块位置
    val bpos: Long,
    //状态
    val state: Int,
) : C2CPacket {


    companion object : ReadablePacket<SetBlockC2CPacket> {

        //从buf读
        override fun read(buf: FriendlyByteBuf): SetBlockC2CPacket {
            return SetBlockC2CPacket(
                buf.readByte().toInt(),
                buf.readLong(),
                buf.readVarInt(),
            )
        }
    }
    override fun process(clientAddress: InetSocketAddress) {
        C2CPacket.sendPacketToRoomAll(clientAddress,this)
    }

    override fun write(buf: FriendlyByteBuf) {
        buf.writeByte(levelId)
        buf.writeLong(bpos)
        buf.writeVarInt(state)
    }

}
