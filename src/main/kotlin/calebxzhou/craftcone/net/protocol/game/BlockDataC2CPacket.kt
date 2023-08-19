package calebxzhou.craftcone.net.protocol.game

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.BufferReadable
import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.InRoomProcessable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.server.entity.ConeBlockPos
import calebxzhou.craftcone.server.entity.ConePlayer
import calebxzhou.craftcone.server.entity.ConeRoom

/**
 * Created  on 2023-07-17,17:16.
 */
//请求方块响应
data class BlockDataC2CPacket(
    //维度ID
    val dimId: Int,
    //方块位置
    val bpos: ConeBlockPos,
    //状态ID
    val stateId: Int,
    //nbt
    val tag: String?
) : Packet, BufferWritable,InRoomProcessable {
    companion object : BufferReadable<BlockDataC2CPacket> {
        override fun read(buf: FriendlyByteBuf) = BlockDataC2CPacket(
            buf.readVarInt(),
            ConeBlockPos(buf.readLong()),
            buf.readVarInt(),
            buf.readUtf()
        )

    }



    override fun write(buf: FriendlyByteBuf) {
        buf.writeVarInt(dimId)
        buf.writeLong(bpos.asLong)
        buf.writeVarInt(stateId)
        buf.writeUtf(tag?:"")
    }

    override fun process(player: ConePlayer, playingRoom: ConeRoom) {
        playingRoom.broadcastPacket(this,player)
        //只有save chunk才保存方块
        playingRoom.writeBlock(this)
    }


}
