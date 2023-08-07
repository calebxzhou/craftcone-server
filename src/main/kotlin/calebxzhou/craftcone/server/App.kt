package calebxzhou.craftcone.server

import calebxzhou.craftcone.server.table.BlockStateTable
import calebxzhou.craftcone.server.table.PlayerInfoTable
import calebxzhou.craftcone.server.table.RoomInfoTable
import mu.KotlinLogging
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Slf4jSqlDebugLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction


val logger = KotlinLogging.logger {}
fun main(args: Array<String>) {
    logger.info { args.contentToString() }
    Database.connect("jdbc:sqlite:data.db", driver = "org.sqlite.JDBC")
    transaction {
        addLogger(Slf4jSqlDebugLogger)
        logger.info { "初始化数据结构" }
        SchemaUtils.create(PlayerInfoTable,RoomInfoTable,BlockStateTable)
    }
    ConeServer.start(19198)
}
