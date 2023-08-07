package calebxzhou.craftcone.server.table

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

/**
 * Created  on 2023-08-07,12:06.
 */
class RoomInfoRow(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object: UUIDEntityClass<RoomInfoRow>(RoomInfoTable)
    var name by RoomInfoTable.name
    var ownerId by RoomInfoTable.ownerId
    var mcVersion by RoomInfoTable.mcVersion
    var isFabric by RoomInfoTable.isFabric
    var isCreative by RoomInfoTable.isCreative
    var blockStateAmount by RoomInfoTable.blockStateAmount
    var seed by RoomInfoTable.seed
    var createTime by RoomInfoTable.createTime
}