

/*

val RID = "64f07fed12698d58d364d256".run { ObjectId(this) }

suspend fun main() {
    ConeServer
    join()
    write()
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

                ConeRoom.getRunningRoom(RID)?.readBlock(0, ConeChunkPos(x, z).asInt) {
                    //print("${ConeBlockPos(it.blockPos)} ${it.chunkPos} \t" )
                    ++readCount
                }
        }
    println(readCount)
}
suspend fun write(){
    for(x in 0..BLOCK_COUNT)
        for(z in 0..BLOCK_COUNT) {
            ConePacketProcessor.procScope.launch {
                val bpos = ConeBlockPos(x, 64, z).asLong
                ConeRoom.getRunningRoom(RID)?.writeBlock(BlockDataC2CPacket(0, bpos, 0, ""))
                print("$x $z \t")
            }

        }
}
suspend fun join(){
    ConeRoom.onPlayerJoin(ConePlayer(ObjectId(),"test","","",0), RID)

}
suspend fun create(){
    ConeRoom.onPlayerCreate(ConePlayer(ObjectId(),"test","","",0), CreateRoomC2SPacket("test","1.20.1",true,0))

}*/
