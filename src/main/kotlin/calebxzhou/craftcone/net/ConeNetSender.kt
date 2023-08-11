package calebxzhou.craftcone.net

import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.server.ConeServer
import calebxzhou.craftcone.server.entity.Player
import calebxzhou.craftcone.server.logger
import io.netty.buffer.PooledByteBufAllocator
import io.netty.channel.socket.DatagramPacket
import java.net.InetSocketAddress

/**
 * Created  on 2023-07-18,21:14.
 */
object ConeNetSender {
    @JvmStatic
    fun sendPacket(packet: BufferWritable, player: Player){
        sendPacket(packet, player.addr)
    }
    @JvmStatic
    fun sendPacket(packet: BufferWritable, clientAddress: InetSocketAddress) {
        val data = FriendlyByteBuf(PooledByteBufAllocator.DEFAULT.directBuffer())
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