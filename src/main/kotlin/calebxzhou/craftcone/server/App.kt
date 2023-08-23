package calebxzhou.craftcone.server

import calebxzhou.craftcone.server.table.BlockStateTable
import calebxzhou.craftcone.server.table.PlayerInfoTable
import calebxzhou.craftcone.server.table.RoomInfoTable
import calebxzhou.craftcone.server.table.RoomSavedChunksTable
import com.microsoft.sqlserver.jdbc.SQLServerDriver
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import mu.KotlinLogging
import oracle.jdbc.OracleDriver
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Slf4jSqlDebugLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.FileWriter
import java.sql.DriverManager
import java.util.*


val logger = KotlinLogging.logger {}
const val PROPS_FILE_NAME = "settings.prop"
val DEFAULT_PROPS = Properties().apply {
    this += "cone.port" to 19198
    this += "dataSource.jdbcUrl" to "jdbc:sqlite:data.db"

}

fun main(args: Array<String>) {
    DriverManager.registerDriver(org.postgresql.Driver())
    DriverManager.registerDriver(org.mariadb.jdbc.Driver())
    DriverManager.registerDriver(org.sqlite.JDBC())
    DriverManager.registerDriver(org.h2.Driver())
    DriverManager.registerDriver(SQLServerDriver())
    DriverManager.registerDriver(OracleDriver())
    logger.info { "读取配置文件中 $PROPS_FILE_NAME" }
    val props = try {
        Properties().apply { load(FileReader(PROPS_FILE_NAME)) }
    }catch (e: FileNotFoundException) {
        logger.error { "找不到配置文件，创建新的" }
        DEFAULT_PROPS.also { it.store(FileWriter(PROPS_FILE_NAME), "") }
    } catch (e: Exception) {
        logger.error { "启动失败" }
        e.printStackTrace()
        return
    }
    HikariConfig(props).also {
        Database.connect(HikariDataSource(it))
    }
    transaction {
        addLogger(Slf4jSqlDebugLogger)
        logger.info { "初始化数据结构" }
        SchemaUtils.create(PlayerInfoTable, RoomInfoTable, BlockStateTable, RoomSavedChunksTable)
    }
    ConeServer.start(props["port"].toString().toInt())
}
