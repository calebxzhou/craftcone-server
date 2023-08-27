package calebxzhou.craftcone.server

import calebxzhou.craftcone.server.entity.ConePlayer
import calebxzhou.craftcone.server.entity.ConeRoom
import com.akuleshov7.ktoml.Toml
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import mu.KotlinLogging
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.io.path.exists


val logger = KotlinLogging.logger {}
const val VERSION_NUM = 3
const val CONF_FILE_NAME = "conf.toml"

suspend fun main(args: Array<String>) {
    logger.info { "读取配置文件中 $CONF_FILE_NAME" }
    loadConfig().run {
        val mongoClient = MongoClient.create(db.url)
        val database = mongoClient.getDatabase(db.dbName)
        initDb(database)
        val playersCollection = database.getCollection<ConePlayer>(ConePlayer.collectionName)
        val roomsCollection = database.getCollection<ConeRoom>(ConeRoom.collectionName)
        ConeServer.start(port,)
    }
    /*loadConfig().run {
        HikariConfig().run {
            username = db.usr
            password = db.pwd
            jdbcUrl = db.url
            minimumIdle = db.minConn
            maximumPoolSize = db.maxConn
            maxLifetime = db.connLife
            Database.connect(HikariDataSource(this))
        }
        createTables()
        ConeServer.start(port)
    }*/

}

suspend fun initDb(db: MongoDatabase) {
    logger.info { "初始化数据结构" }
    db.createCollection(ConePlayer.collectionName)
    db.createCollection(ConeRoom.collectionName)
}

fun loadConfig() = Path(CONF_FILE_NAME).run {
    if (!exists()) {
        logger.info { "找不到配置文件，将使用默认配置" }
        ConeServerConfig.default.let {
            saveConfig(it)
            return@run it
        }
    }
    return@run try { Toml()
        .decodeFromString<ConeServerConfig>(Files.readString(this))
    }catch (e : Exception){
        logger.error{ "解析配置文件失败" }
        throw e
    }
}


fun saveConfig(config: ConeServerConfig) = Toml().encodeToString(config).run {
    Files.writeString(Path(CONF_FILE_NAME), this)
}

