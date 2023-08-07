package calebxzhou.craftcone.server

import calebxzhou.craftcone.net.ConeNetReceiver
import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioDatagramChannel


/**
 * Created  on 2023-07-18,21:02.
 */
object ConeServer {

    lateinit var channelFuture: ChannelFuture

    private val workerGroup: EventLoopGroup = NioEventLoopGroup()
    @JvmStatic
    fun start(port: Int){
        logger.info { "在$port 端口启动服务器" }
        try {
            val b = Bootstrap()
                .group(workerGroup)
                .channel(NioDatagramChannel::class.java)
                .option(ChannelOption.SO_BROADCAST,true)
                .handler(ConeNetReceiver)
            // Bind and start to accept incoming connections.
            channelFuture = b.bind(port).sync()
            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            channelFuture.channel().closeFuture().sync()
        } finally {
            workerGroup.shutdownGracefully()
        }
    }
}