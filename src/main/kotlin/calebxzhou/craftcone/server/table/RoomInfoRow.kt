package calebxzhou.craftcone.server.table

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

/**
 * Created  on 2023-08-07,12:06.
 */
class RoomInfoRow(id: EntityID<Int>) : IntEntity(id) {
    companion object: IntEntityClass<RoomInfoRow>(RoomInfoTable)
    var name by RoomInfoTable.name
    var ownerId by RoomInfoTable.ownerId
    var mcVersion by RoomInfoTable.mcVersion
    var isFabric by RoomInfoTable.isFabric
    var isCreative by RoomInfoTable.isCreative
    var blockStateAmount by RoomInfoTable.blockStateAmount
    var seed by RoomInfoTable.seed
    var createTime by RoomInfoTable.createTime
}