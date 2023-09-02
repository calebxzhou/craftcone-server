import calebxzhou.craftcone.net.protocol.room.CreateRoomC2SPacket
import calebxzhou.craftcone.server.entity.*
import calebxzhou.craftcone.server.logger
import kotlinx.coroutines.runBlocking
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

/**
 * Created  on 2023-08-22,18:52.
0*/

class Test {
    val testId = ObjectId("64f07fed12698d58d364d256")
    val testPlayer = ConePlayer(testId, "testPlayer", "testPwd", "2@2.2", 0)
    val chunks = 32
    val blocks = chunks * 16
    @Test
    fun createRoom() {
        runBlocking {
            ConeRoom.create(testId, testPlayer, CreateRoomC2SPacket("test", "1.20.1", true, 0))
        }
    }
    @Test
    fun bulkInsertBlock(){
        runBlocking {
            ConeRoom.onPlayerJoin(testPlayer,testId)
            for(x in 0..blocks)
                for(z in 0 .. blocks){
                    val letters = ('a'..'z') + ('A'..'Z')
                    val str = (1..2048)
                        .map { letters.random() }
                        .joinToString(separator = "")

                    ConeBlockPos(x,64,z).run {
                        logger.info{this}
                        ConeBlockData(
                            testId,
                            0,
                            chunkPos.asInt,
                            asLong,
                            Random.nextInt(),
                            str).write()
                    }
                }
        }
    }
    @Test
    fun bulkReadBlock(){
        var read=AtomicInteger()
        runBlocking {
            ConeRoom.onPlayerJoin(testPlayer,testId)
            for(x in 0..chunks)
                for(z in 0..chunks){
                    ConeChunkPos(x,z).let { cpos->
                        logger.info { cpos }
                        ConeBlockData.read(testId,0,cpos.asInt){
                            read.incrementAndGet()
                        }

                    }
                }
        }
        println(read)
    }
    @Test
    fun deleteTestBlockData(){
        runBlocking {
            ConeBlockData.clearByRoomId(testId)
        }
    }
}