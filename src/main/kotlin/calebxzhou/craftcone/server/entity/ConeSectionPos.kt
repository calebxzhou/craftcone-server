package calebxzhou.craftcone.server.entity

/**
 * Created  on 2023-08-06,13:49.
 */
data class ConeSectionPos(val x:Int, val y:Int, val z:Int){
    constructor(l: Long) : this((l shl 0 shr 42).toInt(), (l shl 44 shr 44).toInt(), (l shl 22 shr 42).toInt())
    //constructor(string: String) : this(string.split(",")[0].toInt(),string.split(",")[1].toInt(),string.split(",")[2].toInt())
    val asLong: Long
        get() = run {
            var l = 0L
            l = l or (x.toLong() and 0b1111111111111111111111L shl 42)
            l = l or (y.toLong() and 0b11111111111111111111L shl 0)
            l = l or (z.toLong() and 0b1111111111111111111111L shl 20)
            return l
        }

    override fun toString(): String {
        return "$x,$y,$z"
    }
}
