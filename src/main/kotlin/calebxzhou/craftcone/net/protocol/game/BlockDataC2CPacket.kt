package calebxzhou.craftcone.net.protocol.game

import calebxzhou.craftcone.net.ConeByteBuf
import calebxzhou.craftcone.net.ConeNetSender.sendPacketToAll
import calebxzhou.craftcone.net.protocol.BufferReadable
import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.InRoomProcessable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.server.entity.ConeBlockData
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
    val bposl: Long,
    //状态ID
    val stateId: Int,
    //nbt
    val tag: String?
) : Packet, BufferWritable,InRoomProcessable {
    companion object : BufferReadable<BlockDataC2CPacket> {
        override fun read(buf: ConeByteBuf) = BlockDataC2CPacket(
            buf.readVarInt(),
            buf.readLong(),
            buf.readVarInt(),
            buf.readUtf()
        )

    }



    override fun write(buf: ConeByteBuf) {
        buf.writeVarInt(dimId)
        buf.writeLong(bposl)
        buf.writeVarInt(stateId)
        buf.writeUtf(tag?:"")
    }

    override suspend fun process(player: ConePlayer, playingRoom: ConeRoom) {
        playingRoom.sendPacketToAll(player,this)
        ConeBlockData(
            playingRoom.id,
            dimId,
            ConeBlockPos(bposl).chunkPos.asInt,
            bposl,
            stateId,
            tag
        ).write()
    }


}
