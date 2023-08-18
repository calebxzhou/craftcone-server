package calebxzhou.craftcone.server.table

import org.jetbrains.exposed.sql.Table

/**
 * Created  on 2023-08-07,7:17.
 */
object BlockStateTable: Table("block_state") {
    val roomId = integer("room_id").references(RoomInfoTable.id)
    val dimId = integer("dim_id")
    val blockPos = long("block_pos")
    val chunkPos = integer("chunk_pos")
    val blockStateId = integer("block_state_id")
    val tag = varchar("tag",2048).nullable()

    override val primaryKey = PrimaryKey(roomId,dimId, blockPos, name = "PK_RoomDimBpos")
    init {
        index(false, roomId, dimId, chunkPos)
    }
}/*
class BlockState(roomId: EntityID<UUID>,dimId:EntityID<Int>,blockPos: EntityID<Long>) : Entity{
    companion object: UUIDEntityClass<BlockState>(BlockStateTable)
    var roomId by BlockStateTable.roomId
}*/