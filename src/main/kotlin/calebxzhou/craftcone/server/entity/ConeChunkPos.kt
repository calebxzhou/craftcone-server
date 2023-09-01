package calebxzhou.craftcone.server.entity

/**
 * Created  on 2023-08-06,14:09.
 */
data class ConeChunkPos( var x:Int,  var z:Int){
    val asInt = x shl 16 or (z and 0xFFFF)
    override fun toString(): String {
        return "$x,$z"
    }

}
