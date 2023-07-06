package calebxzhou.craftcone.server.net

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.socket.DatagramPacket
import io.netty.util.ReferenceCountUtil
import mu.KotlinLogging
import java.net.InetSocketAddress


val LOG = KotlinLogging.logger {}

/**
 * Created  on 2023-07-03,9:14.
 */
class ConeServerChannelHandler : SimpleChannelInboundHandler<DatagramPacket>() {

    companion object{
        private val clientAddrs = mutableListOf<InetSocketAddress>()
    }
    override fun channelRead0(ctx: ChannelHandlerContext, msg: DatagramPacket) {
        val clientAddr = msg.sender()
        if(!clientAddrs.contains(clientAddr)){
            handleNewConnection(clientAddr)
        }
        try{
            clientAddrs.forEach { addr ->
                if(clientAddr != addr){
                    msg.retain()
                    ctx.writeAndFlush(DatagramPacket(msg.content(),addr))
               }
            }
        }finally {
            //ReferenceCountUtil.safeRelease(msg)
        }
    }

    private fun handleNewConnection(clientAddr: InetSocketAddress) {
        clientAddrs+=clientAddr
        LOG.info("New client: $clientAddr")
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) { // (4)
        // Close the connection when an exception is raised.
        cause.printStackTrace()
        ctx.close()
    }

    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        ctx.flush()
    }


}