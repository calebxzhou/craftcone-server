package calebxzhou.craftcone.server

import kotlinx.serialization.Serializable
import java.time.Duration

/**
 * Created  on 2023-08-24,9:09.
 */
@Serializable
data class ConeServerConfig(
    val port:Int,
    val db :Db,
    val info:Info
){
    companion object{

        val default = ConeServerConfig(19198,
            Db("jdbc:h2:./default_data.db","","",1,1, Duration.ofMinutes(30).toMillis())
            ,Info(500,"CraftCone Server","Default Description","")
        )
    }

    @Serializable
    data class Db(
        val url:String,
        val usr:String,
        val pwd:String,
        val minConn:Int,
        val maxConn:Int,
        val connLife:Long,
    )
    @Serializable
    data class Info(
        val maxPlayerAmountLimit:Int,
        val serverName:String,
        val serverDescription:String,
        val serverIconBase64:String,
    )
}
