package calebxzhou.craftcone.net.protocol.room

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.C2SPacket
import calebxzhou.craftcone.net.protocol.ReadablePacket
import calebxzhou.craftcone.server.LOG
import calebxzhou.craftcone.server.PlayerManager
import calebxzhou.craftcone.server.RoomManager
import java.net.InetSocketAddress
import java.util.*

/**
 * Created  on 2023-07-06,8:48.
 */
//玩家请求加入房间
data class PlayerJoinRoomC2SPacket(
    val rid: UUID
): C2SPacket {
    companion object : ReadablePacket<PlayerJoinRoomC2SPacket>{
        override fun read(buf: FriendlyByteBuf): PlayerJoinRoomC2SPacket {
            return PlayerJoinRoomC2SPacket(buf.readUUID())
        }

    }

    override fun process(clientAddress: InetSocketAddress) {
        //将玩家放入room playerList。并写回room info
        val player = PlayerManager.getByAddr(clientAddress)?:let{
            LOG.warn { "$clientAddress 未登录就请求加入房间了" }
            return
        }
        //房间不在线，检查是否存在
        if (!RoomManager.onlineRooms.containsKey(rid)){
            //存在则载入房间

            //不存在提示失败
        }
    }


}