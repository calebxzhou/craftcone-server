package calebxzhou.craftcone.net

import calebxzhou.craftcone.net.protocol.ConePacketSet
import calebxzhou.craftcone.net.protocol.C2SPacket
import calebxzhou.craftcone.server.ConeServer
import calebxzhou.craftcone.server.LOG
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
        val packetType: Int
        val packetId = when (packet) {
            is InGamePacket -> {
                packetType = InGamePacket.PacketTypeNumber
                ConePacketSet.InGame.getPacketId(packet.javaClass)
            }

            is C2SPacket -> {
                packetType = C2SPacket.PacketTypeNumber
                ConePacketSet.OutGame.getPacketId(packet.javaClass)
            }

            else -> {
                throw IllegalArgumentException("cone packet必须是in game || out game")
            }
        }?: let{
            LOG.error("找不到$packet 对应的包ID")
            return
        }
        //第1个byte，1st bit是包类型（ingame outgame)，剩下7bit是包ID
        val byte1 = (packetType shl 7) or packetId
        data.writeByte(byte1)
        //写入包数据
        packet.write(data)
        //发走
        val udpPacket = DatagramPacket(data,clientAddress)
        ConeServer.channelFuture.channel().writeAndFlush(udpPacket)
    }
}