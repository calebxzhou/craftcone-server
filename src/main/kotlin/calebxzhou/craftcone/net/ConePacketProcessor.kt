package calebxzhou.craftcone.net

import calebxzhou.craftcone.net.protocol.AfterLoginProcessable
import calebxzhou.craftcone.net.protocol.BeforeLoginProcessable
import calebxzhou.craftcone.net.protocol.InRoomProcessable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.server.entity.ConePlayer
import calebxzhou.craftcone.server.entity.ConeRoom
import calebxzhou.craftcone.server.logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.net.InetSocketAddress

/**
 * Created  on 2023-08-26,11:02.
 */
object ConePacketProcessor {
    private val procScope = CoroutineScope(Dispatchers.Default)
    //服务端处理包
     fun processPacket(clientAddr: InetSocketAddress, packet: Packet) = procScope.run{
        when(packet){
            is BeforeLoginProcessable ->{
                packet.process(clientAddr)
            }
            is AfterLoginProcessable ->{
                val player = ConePlayer.getByAddr(clientAddr) ?: let {
                    logger.error { "$clientAddr 想要处理包 ${packet.javaClass.simpleName} 但是此人未登录" }
                    return
                }
                packet.process(player)
            }
            is InRoomProcessable ->{
                val player = ConePlayer.getByAddr(clientAddr) ?: let {
                    logger.error { "$clientAddr 想要处理包 ${packet.javaClass.simpleName} 但是此人未登录" }
                    return
                }
                val room = ConeRoom.getPlayerPlayingRoom(player.id) ?: let {
                    logger.error { "$player 想要处理包 ${packet.javaClass.simpleName} 但是此人未加入任何房间" }
                    return
                }
                packet.process(player,room)
            }
        }
    }

}