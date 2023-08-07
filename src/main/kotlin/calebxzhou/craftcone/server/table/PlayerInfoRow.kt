package calebxzhou.craftcone.server.table

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

/**
 * Created  on 2023-08-07,10:17.
 */
class PlayerInfoRow (id: EntityID<UUID>) : UUIDEntity(id) {
    companion object: UUIDEntityClass<PlayerInfoRow>(PlayerInfoTable)
    var name by PlayerInfoTable.name
    var pwd by PlayerInfoTable.pwd
    var createTime by PlayerInfoTable.createTime

}