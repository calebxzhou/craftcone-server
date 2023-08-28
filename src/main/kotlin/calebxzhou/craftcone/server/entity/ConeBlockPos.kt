package calebxzhou.craftcone.server.entity

/**
 * Created  on 2023-08-06,13:49.
 */
data class ConeBlockPos(val x:Int, val y:Int, val z:Int){
    constructor(long: Long) : this((long shr 38).toInt(), (long shl 52 shr 52).toInt(), (long shl 26 shr 38).toInt())
    constructor(string: String) : this(string.split(",")[0].toInt(),string.split(",")[1].toInt(),string.split(",")[2].toInt())
    val asLong = ((x and 0x3FFFFFF).toLong() shl 38) or ((z and 0x3FFFFFF).toLong() shl 12) or (y.toLong() and 0xFFF)

    val chunkPos
        get() = ConeChunkPos((x shr 4),(z shr 4))
    val sectionPos
        get() = ConeSectionPos(x shr 4,y shr 4,z shr 4)
    override fun toString(): String {
        return "$x,$y,$z"
    }

}
