package calebxzhou.craftcone.server.entity

import calebxzhou.craftcone.net.protocol.game.BlockDataC2CPacket

/**
 * Created  on 2023-08-27,8:10.
 */
data class ConeBlockData(
    val dimId:Int,
    val blockPos: Long,
    val chunkPos: Int,
    val blockStateId:Int,
    val tag:String?
){
    val  dto
        get() = BlockDataC2CPacket(dimId,ConeBlockPos(blockPos),blockStateId,tag)
}
