package calebxzhou.craftcone.net

import calebxzhou.craftcone.net.protocol.ConePacketSet
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
        ConePacketSet.createPacketAndProcess(packetId,clientAddr,data)
        try{
            /*
            给房间里每个客户端发包
            clientAddrs.forEach { addr ->
                if(clientAddr != addr){
                    msg.retain()
                    ctx.writeAndFlush(DatagramPacket(msg.content(),addr))
               }
            }*/
        }finally {
            //ReferenceCountUtil.safeRelease(msg)
        }
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