package calebxzhou.craftcone.net

import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.server.logger
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler


/**
 * Created  on 2023-07-03,9:14.
 */
class ConeNetReceiver : SimpleChannelInboundHandler<Packet>() {
    override fun channelRead0(ctx: ChannelHandlerContext, msg: Packet){
        ConePacketProcessor.processPacket(ctx,msg)
    }


    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) { // (4)
        // Close the connection when an exception is raised.
        logger.error { "Network packet channel error:" }
        cause.printStackTrace()
        ctx.close()
    }

    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        ctx.flush()
    }
}