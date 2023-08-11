package calebxzhou.craftcone.server.table

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

/**
 * Created  on 2023-08-07,10:17.
 */
class PlayerInfoRow (id: EntityID<Int>) : IntEntity(id) {
    companion object: IntEntityClass<PlayerInfoRow>(PlayerInfoTable)
    var name by PlayerInfoTable.name
    var pwd by PlayerInfoTable.pwd
    var createTime by PlayerInfoTable.createTime

}