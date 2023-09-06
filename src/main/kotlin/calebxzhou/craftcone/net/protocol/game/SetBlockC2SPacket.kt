package calebxzhou.craftcone.net.protocol.game

import calebxzhou.craftcone.net.ConeNetSender.sendPacketToAll
import calebxzhou.craftcone.net.protocol.BufferReadable
import calebxzhou.craftcone.net.protocol.InRoomProcessable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.server.entity.ConeBlockData
import calebxzhou.craftcone.server.entity.ConeBlockPos
import calebxzhou.craftcone.server.entity.ConeOnlinePlayer
import calebxzhou.craftcone.server.entity.ConeRoom
import calebxzhou.craftcone.util.ByteBufUt.readUtf
import calebxzhou.craftcone.util.ByteBufUt.readVarInt
import io.netty.buffer.ByteBuf

/**
 * Created  on 2023-07-17,17:16.
 */
data class SetBlockC2SPacket(
    //维度ID
    val dimId: Int,
    //方块位置
    val bposl: Long,
    //状态ID
    val stateId: Int,
    //nbt
    val tag: String?
) : Packet,InRoomProcessable {
    companion object : BufferReadable<SetBlockC2SPacket> {
        override fun read(buf: ByteBuf) = SetBlockC2SPacket(
            buf.readVarInt(),
            buf.readLong(),
            buf.readVarInt(),
            buf.readUtf()
        )

    }
    override suspend fun process(player: ConeOnlinePlayer, playingRoom: ConeRoom) {
        playingRoom.sendPacketToAll(player,BlockDataS2CPacket(dimId, bposl, stateId, tag))
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
