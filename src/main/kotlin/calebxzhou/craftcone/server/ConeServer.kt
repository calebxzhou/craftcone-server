package calebxzhou.craftcone.server

import calebxzhou.craftcone.net.ConeNetDecoder
import calebxzhou.craftcone.net.ConeNetEncoder
import calebxzhou.craftcone.net.ConeNetReceiver
import calebxzhou.craftcone.server.entity.ConeBlockData
import calebxzhou.craftcone.server.entity.ConePlayer
import calebxzhou.craftcone.server.entity.ConeRoom
import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.Indexes
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.DatagramChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
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
                        createIndex(Indexes.hashed("roomId"))
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
        channelFuture = ServerBootstrap()
            .group(NioEventLoopGroup(),NioEventLoopGroup())
            .channel(NioServerSocketChannel::class.java)
            .option(ChannelOption.SO_BACKLOG, 1024)
            .option(ChannelOption.AUTO_CLOSE, true)
            .option(ChannelOption.SO_REUSEADDR, true)
            .childOption(ChannelOption.SO_KEEPALIVE, true)
            .childOption(ChannelOption.TCP_NODELAY, true)
            .childHandler(object : ChannelInitializer<DatagramChannel>() {
                override fun initChannel(ch: DatagramChannel) {
                    ch.pipeline()
                        .addLast(LengthFieldBasedFrameDecoder(65536,0,2,0,2))
                        .addLast(ConeNetDecoder())
                        .addLast(ConeNetReceiver())
                        .addLast(ConeNetEncoder())

                }

            })

            .bind(PORT)
            .syncUninterruptibly()

    }
}

suspend fun main(args: Array<String>) {
    ConeServer
}