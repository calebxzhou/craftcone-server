package calebxzhou.craftcone.server.table

import org.jetbrains.exposed.dao.id.UUIDTable

/**
 * Created  on 2023-08-07,6:57.
 */
object RoomInfoTable : UUIDTable("room_info") {
    val name = varchar("name",50)
    val ownerId = uuid("owner_id")
    val mcVersion = varchar("mc_version",6)
    val isFabric = bool("is_fabric")
    val isCreative = bool("is_creative")
    val blockStateAmount = integer("block_state_amount")
    val seed = long("seed")
    val createTime = long("create_time")
}