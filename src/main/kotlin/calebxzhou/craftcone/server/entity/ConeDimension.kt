package calebxzhou.craftcone.server.entity

/**
 * Created  on 2023-08-28,20:40.
 */
data class ConeDimension(
    val dimId: Int,
    val blockData: List<ConeBlockData> = arrayListOf()
)
