package calebxzhou.craftcone.server

import kotlinx.serialization.Serializable

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
            Db("mongodb://127.0.0.1:27017/","craftcone")
            ,Info(500,"CraftCone Server","Default Description","")
        )
    }

    @Serializable
    data class Db(
        val url:String,
        val dbName:String,
    )
    @Serializable
    data class Info(
        val maxPlayerAmountLimit:Int,
        val serverName:String,
        val serverDescription:String,
        val serverIconBase64:String,
    )
}
