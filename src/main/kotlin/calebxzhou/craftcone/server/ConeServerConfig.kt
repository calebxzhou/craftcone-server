package calebxzhou.craftcone.server

import kotlinx.serialization.Serializable
import java.time.Duration
import kotlin.time.DurationUnit

/**
 * Created  on 2023-08-24,9:09.
 */
@Serializable
data class ConeServerConfig(
    val port:Int,
    val db :Db,
){
    companion object{
        val default = ConeServerConfig(19198, Db("jdbc:h2:./default_data.db",null,null,1,1, Duration.ofMinutes(30).toMillis()))
    }

    @Serializable
    data class Db(
        val url:String,
        val usr:String?,
        val pwd:String?,
        val minConn:Int,
        val maxConn:Int,
        val connLife:Long,

    )
}
