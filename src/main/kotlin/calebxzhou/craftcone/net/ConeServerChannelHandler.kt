package calebxzhou.craftcone.net

import calebxzhou.craftcone.net.protocol.C2SPacket
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
        val byte1 = msg.content().readByte().toInt()
        //第1个bit（包类型，in game/out game）
        val bit1 = byte1 shr 7
        //第2~8个bit（包ID）
        val packetId = (byte1 shl 1).toByte().toInt()

        val data = FriendlyByteBuf(msg.content())
        if(bit1 == C2SPacket.PacketTypeNumber){
            //0代表out game数据包
            ConePacketSet.OutGame.createPacket(packetId, data)
        }else{
            //1代表in game数据包
            ConePacketSet.InGame.createPacket(packetId,data)
        }
        packet.process(clientAddr)
        /*if(!clientAddrs.contains(clientAddr)){
            handleNewConnection(clientAddr)
        }*/
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