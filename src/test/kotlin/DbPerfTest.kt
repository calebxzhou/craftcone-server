import calebxzhou.craftcone.net.protocol.game.BlockDataC2CPacket
import calebxzhou.craftcone.net.protocol.room.CreateRoomC2SPacket
import calebxzhou.craftcone.server.ConeServer
import calebxzhou.craftcone.server.entity.ConeBlockPos
import calebxzhou.craftcone.server.entity.ConeChunkPos
import calebxzhou.craftcone.server.entity.ConePlayer
import calebxzhou.craftcone.server.entity.ConeRoom
import calebxzhou.craftcone.server.logger
import org.bson.types.ObjectId


val RID = "64f0093b3a59e67c0c2a705b".run { ObjectId(this) }
suspend fun main() {
    ConeServer
    join()
    read()


}
suspend fun read(){
    for(x in -4..4)
        for(z in -4..4) {
            ConeRoom.getRunningRoom(RID)?.readBlock(0, ConeChunkPos(x,z)){
                logger.info{it}
            }
        }
}
suspend fun write(){
    for(x in -64..64)
        for(z in -64..64) {
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