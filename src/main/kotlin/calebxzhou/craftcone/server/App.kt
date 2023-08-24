package calebxzhou.craftcone.server

import calebxzhou.craftcone.server.table.BlockStateTable
import calebxzhou.craftcone.server.table.PlayerInfoTable
import calebxzhou.craftcone.server.table.RoomInfoTable
import calebxzhou.craftcone.server.table.RoomSavedChunksTable
import com.akuleshov7.ktoml.Toml
import com.microsoft.sqlserver.jdbc.SQLServerDriver
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
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
import kotlin.io.path.exists


val logger = KotlinLogging.logger {}
const val CONF_FILE_NAME = "conf.toml"

fun main(args: Array<String>) {
    DriverManager.registerDriver(org.postgresql.Driver())
    DriverManager.registerDriver(org.mariadb.jdbc.Driver())
    DriverManager.registerDriver(org.sqlite.JDBC())
    DriverManager.registerDriver(org.h2.Driver())
    DriverManager.registerDriver(SQLServerDriver())
    DriverManager.registerDriver(OracleDriver())
    logger.info { "读取配置文件中 $CONF_FILE_NAME" }
    loadConfig().run {
        HikariConfig().apply {
            username = db.usr
            password = db.pwd
            jdbcUrl = db.url
        }.run {
            Database.connect(HikariDataSource(this))
        }
        createTables()
        ConeServer.start(port)
    }

}

fun createTables() = transaction {
    addLogger(Slf4jSqlDebugLogger)
    logger.info { "初始化数据结构" }
    SchemaUtils.create(PlayerInfoTable, RoomInfoTable, BlockStateTable, RoomSavedChunksTable)
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

