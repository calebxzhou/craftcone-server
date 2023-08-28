package calebxzhou.craftcone.net

import calebxzhou.craftcone.server.logger
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.socket.DatagramPacket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


/**
 * Created  on 2023-07-03,9:14.
 */
class ConeNetReceiver : SimpleChannelInboundHandler<DatagramPacket>() {
    override fun channelRead0(ctx: ChannelHandlerContext, msg: DatagramPacket){
        val clientAddr = msg.sender()
        try {
            //第一个byte
            val packetId = msg.content().readByte().toInt()
            val data = FriendlyByteBuf(msg.content())
            ConePacketSet.createAndProcess(clientAddr,packetId,data)
        } catch (e: Exception) {
            exceptionCaught(ctx,e)
        }
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