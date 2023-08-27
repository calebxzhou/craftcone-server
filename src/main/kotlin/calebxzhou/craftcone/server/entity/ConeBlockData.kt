package calebxzhou.craftcone.server.entity

/**
 * Created  on 2023-08-27,8:10.
 */
data class ConeBlockData(
    val blockPos: ConeBlockPos,
    val chunkPos: ConeChunkPos,
    val blockStateId:Int,
    val tag:String
)
