package calebxzhou.craftcone.server

import calebxzhou.craftcone.server.net.ConeServerChannelHandler
import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioDatagramChannel


fun main(args: Array<String>) {

    val workerGroup: EventLoopGroup = NioEventLoopGroup()
    try {
        val b = Bootstrap() // (2)
        b.group(workerGroup)
            .channel(NioDatagramChannel::class.java)
            .option(ChannelOption.SO_BROADCAST,true)
            .handler(ConeServerChannelHandler())
        // Bind and start to accept incoming connections.
        val f: ChannelFuture = b.bind(19198).sync() // (7)

        // Wait until the server socket is closed.
        // In this example, this does not happen, but you can do that to gracefully
        // shut down your server.
        f.channel().closeFuture().sync()
    } finally {
        workerGroup.shutdownGracefully()
    }
}
