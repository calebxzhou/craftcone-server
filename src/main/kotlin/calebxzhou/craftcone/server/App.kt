package calebxzhou.craftcone.server

import mu.KotlinLogging

//目录结构
/*
data
    player

 */

val logger = KotlinLogging.logger {}
fun main(args: Array<String>) {
    ConeServer.start(19198)
}
