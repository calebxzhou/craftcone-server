package calebxzhou.craftcone.net

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.socket.DatagramPacket



/**
 * Created  on 2023-07-03,9:14.
 */
object ConeServerChannelHandler : SimpleChannelInboundHandler<DatagramPacket>() {

    override fun channelRead0(ctx: ChannelHandlerContext, msg: DatagramPacket) {
        val clientAddr = msg.sender()
        //第一个byte
        val packetId = msg.content().readByte().toInt()
        val data = FriendlyByteBuf(msg.content())
        ConePacketSet.createAndProcess(clientAddr,packetId,data)

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