package calebxzhou.craftcone.server.table

import org.jetbrains.exposed.sql.Table

/**
 * Created  on 2023-08-07,7:17.
 */
object RoomSavedChunksTable: Table("room_saved_chunks") {
    val roomId = integer("room_id").references(RoomInfoTable.id)
    val dimId = integer("dim_id")
    val chunkPos = integer("chunk_pos")
    init {
        index(false, roomId, dimId)
    }
}/*
class BlockState(roomId: EntityID<UUID>,dimId:EntityID<Int>,blockPos: EntityID<Long>) : Entity{
    companion object: UUIDEntityClass<BlockState>(BlockStateTable)
    var roomId by BlockStateTable.roomId
}*/