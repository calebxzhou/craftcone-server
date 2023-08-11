package calebxzhou.craftcone.server.entity

/**
 * Created  on 2023-08-06,14:09.
 */
data class ChunkPos(val x:Int,val z:Int){

    //constructor(string: String):this(string.split(",")[0].toInt(),string.split(",")[1].toInt())
    constructor(l: Long) : this(
        (l and 0xFFFFFFFFL).toInt(),
        (l shr 32).toInt()
    )
    constructor(i : Int) : this(
        (i and 0xFFFF).toShort().toInt(),
        (i shr 16).toShort().toInt()
    )
    val asLong
        get() = x.toLong() shl 32 or z.toLong()
    val asInt :Int
        get() = x.toShort().toInt() shl 16 or z.toShort().toInt()
    override fun toString(): String {
        return "$x,$z"
    }

}
