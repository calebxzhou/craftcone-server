package calebxzhou.craftcone.net.protocol

import calebxzhou.craftcone.net.ConeNetManager
import calebxzhou.craftcone.server.entity.ConeRoom
import calebxzhou.craftcone.server.logger
import java.net.InetSocketAddress

/**
 * Created  on 2023-06-29,20:43.
 */
//client -> client包
interface C2CPacket : C2SPacket , WritablePacket  {
    companion object{
        fun sendPacketToRoomAll(senderAddress: InetSocketAddress,packet: WritablePacket){
            val room = ConeRoom.getPlayingRoomByAddr(senderAddress)?:let{
                logger.warn{"$senderAddress 没有游玩房间就发c2c数据包"}
                return
            }
            room.players.forEach {
                ConeNetManager.sendPacket(packet,it.addr)
            }
        }
    }
}