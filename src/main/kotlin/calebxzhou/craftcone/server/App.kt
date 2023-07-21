package calebxzhou.craftcone.server

import mu.KotlinLogging

//目录结构
/*
data
    player

 */

val LOG = KotlinLogging.logger {}
fun main(args: Array<String>) {
    ConeServer.start(19198)
}
