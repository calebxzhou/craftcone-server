package calebxzhou.craftcone.server

import calebxzhou.craftcone.server.table.BlockStateTable
import calebxzhou.craftcone.server.table.PlayerInfoTable
import calebxzhou.craftcone.server.table.RoomInfoTable
import calebxzhou.craftcone.server.table.RoomSavedChunksTable
import com.akuleshov7.ktoml.file.TomlFileReader
import com.microsoft.sqlserver.jdbc.SQLServerDriver
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.serialization.decodeFromString
import mu.KotlinLogging
import oracle.jdbc.OracleDriver
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Slf4jSqlDebugLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import java.nio.file.Files
import java.sql.DriverManager
import kotlin.io.path.Path


val logger = KotlinLogging.logger {}
const val PROPS_FILE_NAME = "settings.prop"

fun main(args: Array<String>) {
    DriverManager.registerDriver(org.postgresql.Driver())
    DriverManager.registerDriver(org.mariadb.jdbc.Driver())
    DriverManager.registerDriver(org.sqlite.JDBC())
    DriverManager.registerDriver(org.h2.Driver())
    DriverManager.registerDriver(SQLServerDriver())
    DriverManager.registerDriver(OracleDriver())
    logger.info { "读取配置文件中 $PROPS_FILE_NAME" }
    val conf = TomlFileReader.decodeFromString<ConeServerConfig>(Files.readString(Path(PROPS_FILE_NAME)))

    HikariConfig().apply {
        username = conf.db.usr
        password = conf.db.pwd
        jdbcUrl
    }.also {
        Database.connect(HikariDataSource(it))
    }
    transaction {
        addLogger(Slf4jSqlDebugLogger)
        logger.info { "初始化数据结构" }
        SchemaUtils.create(PlayerInfoTable, RoomInfoTable, BlockStateTable, RoomSavedChunksTable)
    }
    ConeServer.start(conf.port)
}