package calebxzhou.craftcone.net

import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.MsgLevel
import calebxzhou.craftcone.net.protocol.MsgType
import calebxzhou.craftcone.net.protocol.general.SysMsgS2CPacket
import calebxzhou.craftcone.server.entity.ConeOnlinePlayer
import calebxzhou.craftcone.server.entity.ConeRoom
import calebxzhou.craftcone.server.logger
import io.netty.channel.ChannelHandlerContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created  on 2023-07-18,21:14.
 */
object ConeNetSender {
    private val senderScope = CoroutineScope(Dispatchers.IO)
    @JvmStatic
    fun sendPacket(ctx: ChannelHandlerContext,packet: BufferWritable) = senderScope.launch {
        logger.debug { "start sending packet ${packet.javaClass.simpleName}" }
        ctx.channel().writeAndFlush(packet)
    }

    fun ConeOnlinePlayer.sendPacket(packet: BufferWritable) {
        sendPacket(this.ctx,packet)
    }
    fun ConeRoom.sendPacketToAll(sender:ConeOnlinePlayer,packet: BufferWritable){
        inRoomPlayers.forEach {
            if (sender.ctx != it.value.ctx) {
                sendPacket(it.value.ctx,packet)
            }
        }
    }
}
//客户端错误弹框
fun coneErrDialog(ctx: ChannelHandlerContext, msg:String){
    ConeNetSender.sendPacket(ctx,SysMsgS2CPacket(MsgType.Dialog,MsgLevel.Err,msg))
}
//客户端消息弹框
fun coneInfoToast(ctx: ChannelHandlerContext, msg: String){
    ConeNetSender.sendPacket(ctx,SysMsgS2CPacket(MsgType.Toast,MsgLevel.Info,msg))
}
fun coneInfoDialog(ctx: ChannelHandlerContext,msg:String){
    ConeNetSender.sendPacket(ctx,SysMsgS2CPacket(MsgType.Dialog,MsgLevel.Info,msg))
}
//发包
fun coneSendPacket(ctx: ChannelHandlerContext, packet: BufferWritable){
    ConeNetSender.sendPacket(ctx,packet)
}
