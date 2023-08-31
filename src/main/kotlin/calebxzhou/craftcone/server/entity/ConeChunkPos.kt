package calebxzhou.craftcone.server.entity

/**
 * Created  on 2023-08-06,14:09.
 */
data class ConeChunkPos(@Volatile var x:Int, @Volatile var z:Int){
/*    val asLong
        get() = x.toLong() shl 32 or z.toLong()*/
    val asInt = x shl 16 or (z and 0xFFFF)
    override fun toString(): String {
        return "$x,$z"
    }

}
