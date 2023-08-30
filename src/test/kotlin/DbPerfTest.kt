import calebxzhou.craftcone.server.ConeServer
import calebxzhou.craftcone.server.entity.ConeBlockData
import calebxzhou.craftcone.server.entity.ConeBlockPos
import calebxzhou.craftcone.server.entity.ConeRoom
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Updates
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
    ConeServer
    val t1 = System.currentTimeMillis()
     runBlocking {
         for(x in -32..32)
             for(z in -32..32){
                 GlobalScope.launch {
                     val bpos = ConeBlockPos(x, 64, z)
                     println(bpos)
                     ConeRoom.dbcl.updateOne(
                         eq("_id","64ee9a86e2bd8c4002e16fe1"),
                         Updates.push("blockData",ConeBlockData(0,bpos,bpos.chunkPos.asInt, 1,null))
                     )
                 }

             }
     }
    println(System.currentTimeMillis()-t1)
}