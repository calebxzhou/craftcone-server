package calebxzhou.craftcone.server.table

import org.jetbrains.exposed.dao.id.UUIDTable

/**
 * Created  on 2023-08-07,6:57.
 */
object PlayerInfoTable : UUIDTable("player_info"){
    val name = varchar("name",20)
    val pwd = varchar("pwd",16)
    val createTime = long("create_time")
}