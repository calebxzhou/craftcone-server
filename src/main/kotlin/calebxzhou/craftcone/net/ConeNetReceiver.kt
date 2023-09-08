package calebxzhou.craftcone.net

import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.net.protocol.general.ServerInfoS2CPacket
import calebxzhou.craftcone.server.entity.ConeOnlinePlayer
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

    override fun channelActive(ctx: ChannelHandlerContext) {
        logger.info { "${ctx.channel().remoteAddress()} connected" }
        ConeNetSender.sendPacket(ctx,ServerInfoS2CPacket)
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        logger.info { "${ctx.channel().remoteAddress()} disconnected" }
        ConeOnlinePlayer.getByNetCtx(ctx)?.let { ConeOnlinePlayer.goOffline(it) }
    }

}