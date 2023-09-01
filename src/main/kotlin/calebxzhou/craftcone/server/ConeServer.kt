package calebxzhou.craftcone.server

import calebxzhou.craftcone.net.ConeNetReceiver
import calebxzhou.craftcone.server.entity.ConeBlockData
import calebxzhou.craftcone.server.entity.ConePlayer
import calebxzhou.craftcone.server.entity.ConeRoom
import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.Indexes
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
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
                    mdb.getCollection<ConePlayer>(this).apply {
                        //player name index
                        createIndex(
                            Indexes.ascending(ConePlayer::name.name),
                            IndexOptions().unique(true)
                        )
                    }
                }
                ConeRoom.collectionName.run {
                    mdb.getCollection<ConeRoom>(this).apply {
                        //owner id index
                        createIndex(Indexes.hashed("owner._id"))
                    }
                }
                ConeBlockData.collectionName.run {
                    mdb.getCollection<ConeBlockData>(this).apply {
                        createIndex(
                            Indexes.compoundIndex(
                                Indexes.ascending("roomId"),
                                Indexes.ascending("dimId"),
                                Indexes.ascending("chunkPosi"),
                            )
                        )
                        createIndex(
                            Indexes.compoundIndex(
                                Indexes.ascending("roomId"),
                                Indexes.ascending("dimId"),
                                Indexes.ascending("blockPosl"),
                            )
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


    init {
        logger.info { "Starting CraftCone Server at Port $PORT" }
        channelFuture  = Bootstrap()
                .group(NioEventLoopGroup())
                .channel(NioDatagramChannel::class.java)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(object : ChannelInitializer<DatagramChannel>() {
                    override fun initChannel(ch: DatagramChannel) {
                        ch.pipeline()
                            .addLast(ConeNetReceiver())
                    }

                })
                .bind(PORT)
                .syncUninterruptibly()
    }
}

suspend fun main(args: Array<String>) {
    ConeServer
}