package calebxzhou.craftcone.net

import calebxzhou.craftcone.net.protocol.ConePacketSet
import calebxzhou.craftcone.net.protocol.WritablePacket
import calebxzhou.craftcone.server.ConeServer
import calebxzhou.craftcone.server.logger
import io.netty.buffer.Unpooled
import io.netty.channel.socket.DatagramPacket
import java.net.InetSocketAddress

/**
 * Created  on 2023-07-18,21:14.
 */
object ConeNetManager {
    @JvmStatic
    fun sendPacket(packet: WritablePacket, clientAddress: InetSocketAddress) {
        val data = FriendlyByteBuf(Unpooled.buffer())
        val packetId = ConePacketSet.getPacketId(packet.javaClass)?: let{
            logger.error("找不到$packet 对应的包ID")
            return
        }
        data.writeByte(packetId)
        //写入包数据
        packet.write(data)
        //发走
        val udpPacket = DatagramPacket(data,clientAddress)
        ConeServer.channelFuture.channel().writeAndFlush(udpPacket)
    }
}