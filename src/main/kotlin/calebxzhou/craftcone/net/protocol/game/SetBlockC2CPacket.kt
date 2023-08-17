package calebxzhou.craftcone.net.protocol.game

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.BufferReadable
import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.InRoomProcessable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.server.entity.ConePlayer
import calebxzhou.craftcone.server.entity.ConeRoom

/**
 * Created  on 2023-06-29,20:46.
 */
//玩家请求变更方块状态（玩家破坏+放置）
data class SetBlockC2CPacket(
    //维度ID
    val dimId: Int,
    //方块位置
    val bpos: Long,
    //状态ID
    val stateId: Int,
) : Packet, InRoomProcessable, BufferWritable {


    companion object : BufferReadable<SetBlockC2CPacket> {

        //从buf读
        override fun read(buf: FriendlyByteBuf): SetBlockC2CPacket {
            return SetBlockC2CPacket(
                buf.readByte().toInt(),
                buf.readLong(),
                buf.readVarInt(),
            )
        }
    }

    override fun write(buf: FriendlyByteBuf) {
        buf.writeByte(dimId)
        buf.writeLong(bpos)
        buf.writeVarInt(stateId)
    }




}
