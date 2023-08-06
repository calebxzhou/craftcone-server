package calebxzhou.craftcone.server.entity

/**
 * Created  on 2023-08-06,14:09.
 */
data class ChunkPos(val x:Int,val z:Int){
    //constructor(string: String):this(string.split(",")[0].toInt(),string.split(",")[1].toInt())
    constructor(l: Long) : this(l.toInt(),l.shr(32).toInt())

    val asLong
        get() = x.toLong() and 4294967295L or (z.toLong() and 4294967295L shl 32)
    override fun toString(): String {
        return "$x,$z"
    }

}
