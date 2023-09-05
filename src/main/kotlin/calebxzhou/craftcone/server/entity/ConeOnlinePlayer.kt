package calebxzhou.craftcone.server.entity

import io.netty.channel.ChannelHandlerContext
import org.bson.types.ObjectId

data class ConeOnlinePlayer(val data: ConePlayer,val ctx:ChannelHandlerContext){
    companion object{
        //全部在线玩家
        private val onlinePlayers = hashMapOf<ObjectId, ConeOnlinePlayer>()
        //玩家ip to 玩家
        private val netContextToPlayer = hashMapOf<ChannelHandlerContext, ConeOnlinePlayer>()

        fun goOnline(onlinePlayer: ConeOnlinePlayer){
            onlinePlayers += onlinePlayer.data.id to onlinePlayer
            netContextToPlayer += onlinePlayer.ctx to onlinePlayer
        }
        fun goOffline(onlinePlayer: ConeOnlinePlayer){
            onlinePlayers -= onlinePlayer.data.id
            netContextToPlayer -= onlinePlayer.ctx
        }
    }
}
