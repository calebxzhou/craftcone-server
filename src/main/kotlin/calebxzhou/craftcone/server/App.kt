package calebxzhou.craftcone.server

import calebxzhou.craftcone.server.table.BlockStateTable
import calebxzhou.craftcone.server.table.PlayerInfoTable
import calebxzhou.craftcone.server.table.RoomInfoTable
import calebxzhou.craftcone.server.table.RoomSavedChunksTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import mu.KotlinLogging
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Slf4jSqlDebugLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.util.*
import kotlin.math.log


val logger = KotlinLogging.logger {}
const val PROPS_FILE_NAME = "settings.prop"
val DEFAULT_PROPS = Properties().apply {
    this += "port" to 19198

}
fun main(args: Array<String>) {
    logger.info { "读取配置文件中 $PROPS_FILE_NAME" }
    val props = try {
        Properties().apply { load(FileReader(PROPS_FILE_NAME)) }
    }catch (e : Exception){
        logger.error { "启动失败" }
        when(e){
            is FileNotFoundException -> logger.error { "找不到配置文件" }
        }
        e.printStackTrace()
        return
    }
    HikariConfig(props).also {
        Database.connect(HikariDataSource(it))
    }
    transaction {
        addLogger(Slf4jSqlDebugLogger)
        logger.info { "初始化数据结构" }
        SchemaUtils.create(PlayerInfoTable,RoomInfoTable,BlockStateTable,RoomSavedChunksTable)
    }
    ConeServer.start(props["port"].toString().toInt())
}
