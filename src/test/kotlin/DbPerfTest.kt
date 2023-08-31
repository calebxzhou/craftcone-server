
import calebxzhou.craftcone.net.protocol.game.BlockDataC2CPacket
import calebxzhou.craftcone.net.protocol.room.CreateRoomC2SPacket
import calebxzhou.craftcone.server.ConeServer
import calebxzhou.craftcone.server.entity.ConeBlockPos
import calebxzhou.craftcone.server.entity.ConeChunkPos
import calebxzhou.craftcone.server.entity.ConePlayer
import calebxzhou.craftcone.server.entity.ConeRoom
import calebxzhou.craftcone.server.logger
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates.unset
import org.bson.types.ObjectId


val RID = "64f07fed12698d58d364d256".run { ObjectId(this) }
val CHUNK_COUNT = 1
val BLOCK_COUNT = CHUNK_COUNT * 16
suspend fun main() {
    ConeServer
    join()
    read()
}
suspend fun clear(){
    ConeRoom.dbcl.updateOne(
        Filters.eq("_id",RID),
        unset("blockData")
    )
}
suspend fun read(){
    var readCount = 0

    for(x in 0..CHUNK_COUNT)
        for(z in 0..CHUNK_COUNT) {
            logger.info { "$x $z" }
                ConeRoom.getRunningRoom(RID)?.readBlock(0, ConeChunkPos(x, z)) {
                    //print("${ConeBlockPos(it.blockPos)} ${it.chunkPos} \t" )
                    ++readCount
                }
        }
    println(readCount)
}
suspend fun write(){
    for(x in 0..BLOCK_COUNT)
        for(z in 0..BLOCK_COUNT) {
            val bpos = ConeBlockPos(x, 64, z)
            ConeRoom.getRunningRoom(RID)?.writeBlock(BlockDataC2CPacket(0, bpos, 0, ""))
        }
}
suspend fun join(){
    ConeRoom.onPlayerJoin(ConePlayer(ObjectId(),"test","","",0), RID)

}
suspend fun create(){
    ConeRoom.onPlayerCreate(ConePlayer(ObjectId(),"test","","",0), CreateRoomC2SPacket("test","1.20.1",true,0))

}