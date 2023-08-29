package calebxzhou.craftcone.server

import calebxzhou.craftcone.net.ConeNetReceiver
import calebxzhou.craftcone.server.entity.ConePlayer
import calebxzhou.craftcone.server.entity.ConeRoom
import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.Indexes
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.DatagramChannel
import io.netty.channel.socket.nio.NioDatagramChannel
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging


/**
 * Created  on 2023-07-18,21:02.
 */
val logger = KotlinLogging.logger {}
const val VERSION_NUM = 3

val CONF = ConeServerConfig.loadConfig().also { logger.info("config loaded! $it") }

val PORT = CONF.port
val DB = CONF.run {
    runBlocking {
        try {
            MongoClient.create(db.url).getDatabase(db.dbName).also { mdb ->
                logger.info { "Init Database" }
                ConePlayer.collectionName.run {
                    mdb.createCollection(this)
                    mdb.getCollection<ConePlayer>(this).apply {
                        //player name index
                        createIndex(
                            Indexes.hashed(ConePlayer::name.name),
                            IndexOptions().unique(true)
                        )
                    }
                }
                ConeRoom.collectionName.run {
                    mdb.createCollection(this)
                    mdb.getCollection<ConeRoom>(ConeRoom.collectionName).apply {
                        //owner id index
                        createIndex(Indexes.hashed("owner._id"))
                        //chunk pos index
                        createIndex(
                            Indexes.compoundIndex(
                                Indexes.hashed("dimensions.dimId"),
                                Indexes.hashed("dimensions.blockData.chunkPos"),
                            )
                        )
                        //block pos unique
                        createIndex(
                            Indexes.hashed("dimensions.blockData.blockPos.asLong"),
                            IndexOptions().unique(true)
                        )
                    }
                }
            }
        } catch (e: Exception) {
            logger.error { "failed connect database, check config!" }
            throw e
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
                .option(ChannelOption.SO_BROADCAST, true)
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