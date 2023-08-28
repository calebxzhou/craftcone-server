package calebxzhou.craftcone.server.entity

import calebxzhou.craftcone.net.protocol.game.BlockDataC2CPacket

/**
 * Created  on 2023-08-27,8:10.
 */
data class ConeBlockData(
    val blockPos: ConeBlockPos,
    val chunkPos: ConeChunkPos,
    val blockStateId:Int,
    val tag:String?
){
    fun dto(dimId:Int) = BlockDataC2CPacket(dimId,blockPos,blockStateId,tag)
}
