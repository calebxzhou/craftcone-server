package calebxzhou.craftcone.server

import calebxzhou.craftcone.net.ConeNetReceiver
import calebxzhou.craftcone.server.entity.ConePlayer
import calebxzhou.craftcone.server.entity.ConeRoom
import com.akuleshov7.ktoml.Toml
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.DatagramChannel
import io.netty.channel.socket.nio.NioDatagramChannel
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import mu.KotlinLogging
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.io.path.exists


/**
 * Created  on 2023-07-18,21:02.
 */
val logger = KotlinLogging.logger {}
const val VERSION_NUM = 3

val CONF = ConeServerConfig.loadConfig()

val PORT = CONF.port
val DB = CONF.run {
    runBlocking {
        MongoClient.create(db.url).getDatabase(db.dbName).also {
            logger.info { "Init Database" }
            it.createCollection(ConePlayer.collectionName)
            it.createCollection(ConeRoom.collectionName)
        }
    }
}



object ConeServer {

    val channelFuture: ChannelFuture

    private val workerGroup: EventLoopGroup = NioEventLoopGroup(Runtime.getRuntime().availableProcessors())

    init {
        logger.info { "Starting CraftCone Server at Port $PORT" }
        try {
            val b = Bootstrap()
                .group(workerGroup)
                .channel(NioDatagramChannel::class.java)
                .option(ChannelOption.SO_BROADCAST,true)
                .handler(object : ChannelInitializer<DatagramChannel>() {
                    override fun initChannel(ch: DatagramChannel) {
                        ch.pipeline()
                            .addLast(ConeNetReceiver())
                    }

                })
            channelFuture = b.bind(PORT).sync()
            channelFuture.channel().closeFuture().sync()
        } finally {
            workerGroup.shutdownGracefully()
        }

    }
}
suspend fun main(args: Array<String>) {
    ConeServer
}