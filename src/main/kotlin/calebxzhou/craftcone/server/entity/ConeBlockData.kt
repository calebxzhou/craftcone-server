package calebxzhou.craftcone.server.entity

import calebxzhou.craftcone.net.protocol.game.BlockDataC2CPacket
import calebxzhou.craftcone.server.DB
import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates.set
import org.bson.types.ObjectId

/**
 * Created  on 2023-08-27,8:10.
 */
data class ConeBlockData(
    val roomId: ObjectId,
    val dimId: Int,
    val chunkPosi: Int,
    val blockPosl: Long,
    val blockStateId: Int,
    val tag: String?
) {
    val dto
        get() = BlockDataC2CPacket(dimId, blockPosl, blockStateId, tag)

    companion object {
        const val collectionName = "blocks"
        val dbcl = DB.getCollection<ConeBlockData>(collectionName)

        suspend fun read(roomId: ObjectId,dimId: Int,chunkPosi: Int,doForEachBlock:(ConeBlockData)->Unit){
            dbcl.find(and(
                eq("roomId",roomId),
                eq("dimId",dimId),
                eq("chunkPosi",chunkPosi),
            )).collect(doForEachBlock)
        }
        suspend fun clearByRoomId(roomId: ObjectId){
            dbcl.deleteOne(
                eq("roomId",roomId)
            )
        }
    }

    suspend fun write() {
        dbcl.updateOne(
            and(
                eq("roomId",roomId),
                eq("dimId",dimId),
                eq("blockPosl",blockPosl),
            ),
            listOf(
                set("roomId",roomId),
                set("dimId",dimId),
                set("chunkPosi",chunkPosi),
                set("blockPosl",blockPosl),
                set("blockStateId",blockStateId),
                set("tag",tag),
            ),
            UpdateOptions().upsert(true)
        )
    }

}
