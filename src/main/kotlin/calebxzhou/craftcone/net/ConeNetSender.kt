package calebxzhou.craftcone.net

import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.MsgLevel
import calebxzhou.craftcone.net.protocol.MsgType
import calebxzhou.craftcone.net.protocol.general.SysMsgS2CPacket
import calebxzhou.craftcone.server.ConeServer
import calebxzhou.craftcone.server.entity.ConePlayer
import calebxzhou.craftcone.server.logger
import io.netty.buffer.PooledByteBufAllocator
import io.netty.channel.socket.DatagramPacket
import java.net.InetSocketAddress

/**
 * Created  on 2023-07-18,21:14.
 */
object ConeNetSender {
    @JvmStatic
    fun sendPacket(address: InetSocketAddress,packet: BufferWritable){
        val data = FriendlyByteBuf(PooledByteBufAllocator.DEFAULT.directBuffer())
        val packetId = ConePacketSet.getPacketId(packet.javaClass)?: let{
            logger.error("找不到$packet 对应的包ID")
            return
        }
        data.writeByte(packetId)
        //写入包数据
        packet.write(data)
        //发走
        val udpPacket = DatagramPacket(data,address)
        ConeServer.channelFuture.channel().writeAndFlush(udpPacket)
    }
    @JvmStatic
    fun sendPacket(packet: BufferWritable, player: ConePlayer){
        sendPacket(packet, player.addr)
    }
    @JvmStatic
    fun sendPacket(packet: BufferWritable, clientAddress: InetSocketAddress) {
        sendPacket(clientAddress,packet)
    }
}
//客户端错误弹框
fun coneErrD(player: ConePlayer,msg:String){
    coneErrD(player.addr,msg)
}
fun coneErrD(clientAddress: InetSocketAddress,msg:String){
    ConeNetSender.sendPacket(SysMsgS2CPacket(MsgType.Dialog,MsgLevel.Err,msg),clientAddress)
}
//客户端消息弹框
fun coneInfoT(clientAddress: InetSocketAddress,msg: String){
    ConeNetSender.sendPacket(clientAddress,SysMsgS2CPacket(MsgType.Toast,MsgLevel.Info,msg))
}
//发包
fun coneSenP(clientAddress: InetSocketAddress, packet: BufferWritable){
    ConeNetSender.sendPacket(clientAddress,packet)
}