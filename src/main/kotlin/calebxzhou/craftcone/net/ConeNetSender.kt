package calebxzhou.craftcone.net

import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.MsgLevel
import calebxzhou.craftcone.net.protocol.MsgType
import calebxzhou.craftcone.net.protocol.general.SysMsgS2CPacket
import calebxzhou.craftcone.server.ConeServer
import calebxzhou.craftcone.server.entity.ConeOnlinePlayer
import calebxzhou.craftcone.server.entity.ConePlayer
import calebxzhou.craftcone.server.entity.ConeRoom
import calebxzhou.craftcone.server.logger
import io.netty.buffer.ByteBuf
import io.netty.buffer.PooledByteBufAllocator
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.socket.DatagramPacket
import io.netty.handler.codec.ByteToMessageDecoder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.InetSocketAddress

/**
 * Created  on 2023-07-18,21:14.
 */
object ConeNetSender {
    private val senderScope = CoroutineScope(Dispatchers.IO)
    @JvmStatic
    fun sendPacket(ctx: ChannelHandlerContext,packet: BufferWritable) = senderScope.launch {
        ctx.channel().writeAndFlush(packet)
    }

    fun ConeOnlinePlayer.sendPacket(packet: BufferWritable) {
        sendPacket(this.ctx,packet)
    }
    fun ConeRoom.sendPacketToAll(sender:ConeOnlinePlayer,packet: BufferWritable){
        inRoomPlayers.forEach {
            if (sender.ctx != it.value.ctx) {
                sendPacket(it.value.addr,packet)
            }
        }
    }
}
//客户端错误弹框
fun coneErrDialog(player: ConePlayer, msg:String){
    coneErrDialog(player.addr,msg)
}
fun coneErrDialog(ctx: ChannelHandlerContext, msg:String){
    ConeNetSender.sendPacket(clientAddress,SysMsgS2CPacket(MsgType.Dialog,MsgLevel.Err,msg))
}
//客户端消息弹框
fun coneInfoToast(ctx: ChannelHandlerContext, msg: String){
    ConeNetSender.sendPacket(clientAddress,SysMsgS2CPacket(MsgType.Toast,MsgLevel.Info,msg))
}
fun coneInfoDialog(ctx: ChannelHandlerContext,msg:String){
    ConeNetSender.sendPacket(clientAddress,SysMsgS2CPacket(MsgType.Dialog,MsgLevel.Info,msg))
}
//发包
fun coneSendPacket(ctx: ChannelHandlerContext, packet: BufferWritable){
    ConeNetSender.sendPacket(clientAddress,packet)
}
