package calebxzhou.craftcone.net

import calebxzhou.craftcone.net.protocol.AfterLoginProcessable
import calebxzhou.craftcone.net.protocol.BeforeLoginProcessable
import calebxzhou.craftcone.net.protocol.InRoomProcessable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.server.entity.ConePlayer
import calebxzhou.craftcone.server.entity.ConeRoom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.InetSocketAddress

/**
 * Created  on 2023-08-26,11:02.
 */
object ConePacketProcessor {
    private val procScope = CoroutineScope(Dispatchers.Default)
    //服务端处理包
    fun processPacket(clientAddr: InetSocketAddress, packet: Packet) = procScope.launch{
        when(packet){
            is BeforeLoginProcessable ->{
                packet.process(clientAddr)
            }

            is AfterLoginProcessable -> ConePlayer.getOnlineByAddr(clientAddr)?.run {
                packet.process(this)
            }

            is InRoomProcessable -> ConePlayer.getOnlineByAddr(clientAddr)?.run {
                ConeRoom.getPlayerPlayingRoom(id)?.let {
                    packet.process(this,it)
                }
            }

        }
    }

}