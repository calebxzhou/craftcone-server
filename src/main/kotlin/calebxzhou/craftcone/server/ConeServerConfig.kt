package calebxzhou.craftcone.server

import kotlinx.serialization.Serializable

/**
 * Created  on 2023-08-24,9:09.
 */
@Serializable
data class ConeServerConfig(
    val port:Int,
    val db :Db,
){
    @Serializable
    data class Db(
        val type:Int,
        val usr:String,
        val pwd:String,
        val name:String,
    )
}
