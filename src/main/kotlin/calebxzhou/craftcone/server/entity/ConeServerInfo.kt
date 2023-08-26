package calebxzhou.craftcone.server.entity

import calebxzhou.craftcone.server.VERSION_NUM

/**
 * Created  on 2023-08-26,21:05.
 */
data class ConeServerInfo(
    val version: Int = VERSION_NUM,
    val maxPlayers:Int,
    val onlinePlayers:Int,
    val name:String,
    val descr:String,
    //base64-encoded
    val icon:String,
)
