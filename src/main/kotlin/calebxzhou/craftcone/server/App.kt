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


val logger = KotlinLogging.logger {}
fun main(args: Array<String>) {
    HikariConfig().apply 
        maximumPoolSize = 32768
        minimumIdle = 32
        isAutoCommit = true
        dataSourceProperties += "cachePrepStmts" to "true"
        dataSourceProperties += "prepStmtCacheSize" to "1024"
        dataSourceProperties += "prepStmtCacheSqlLimit" to "8192"
    }.also {
        Database.connect(HikariDataSource(it))
    }
    transaction {
        addLogger(Slf4jSqlDebugLogger)
        logger.info { "初始化数据结构" }
        SchemaUtils.create(PlayerInfoTable,RoomInfoTable,BlockStateTable,RoomSavedChunksTable)
    }
    ConeServer.start(19198)
}
