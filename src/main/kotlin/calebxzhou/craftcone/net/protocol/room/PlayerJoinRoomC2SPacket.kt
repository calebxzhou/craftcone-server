package calebxzhou.craftcone.net.protocol.room

import calebxzhou.craftcone.net.ConeNetManager
import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.C2SPacket
import calebxzhou.craftcone.net.protocol.ReadablePacket
import calebxzhou.craftcone.server.entity.ConePlayer
import calebxzhou.craftcone.server.entity.ConeRoom
import calebxzhou.craftcone.server.logger
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
        val player = ConePlayer.getByAddr(clientAddress)?:let{
            logger.warn { "$clientAddress 未登录就请求加入房间了" }
            return
        }
        //房间是否在线
        if (ConeRoom.isOnline(rid)){
            //在线则直接加入
            ConeRoom.playerJoinRoom(player,rid)
            ConeNetManager.sendPacket(PlayerJoinRoomS2CPacket(true,""),clientAddress)
        }else{
            //离线则查询此房间是否存在
            if(ConeRoom.exists(rid)){
                //存在则载入房间
                ConeRoom.load(rid)
                //然后再加入
                ConeRoom.playerJoinRoom(player, rid)
                ConeNetManager.sendPacket(PlayerJoinRoomS2CPacket(true,""),clientAddress)
            }else{
                //不存在提示失败
                ConeNetManager.sendPacket(PlayerJoinRoomS2CPacket(false,"房间不存在"),clientAddress)
            }
        }

    }


}