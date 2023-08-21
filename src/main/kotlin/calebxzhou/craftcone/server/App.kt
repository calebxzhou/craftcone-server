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
    Database.connect("jdbc:postgresql://sy6.calebxzhou.cn:5432/mc", driver = "org.postgresql.Driver", user = "calebxzhou", password = "rdi")
    transaction {
        addLogger(Slf4jSqlDebugLogger)
        logger.info { "初始化数据结构" }
        SchemaUtils.create(PlayerInfoTable,RoomInfoTable,BlockStateTable)
    }
    ConeServer.start(19198)
}
