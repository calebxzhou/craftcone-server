package calebxzhou.craftcone.server.entity

import io.netty.channel.ChannelHandlerContext
import org.bson.types.ObjectId

data class ConeOnlinePlayer(val data: ConePlayer,val ctx:ChannelHandlerContext){
    override fun toString(): String {
        return data.toString()
    }
    companion object{
        //全部在线玩家
        private val onlinePlayers = hashMapOf<ObjectId, ConeOnlinePlayer>()
        //玩家ip to 玩家
        private val netContextToPlayer = hashMapOf<ChannelHandlerContext, ConeOnlinePlayer>()
        val onlinePlayerCount
            get() = onlinePlayers.size
        //根据ip地址获取在线玩家
        fun getByNetCtx(ctx :ChannelHandlerContext): ConeOnlinePlayer? = netContextToPlayer[ctx]
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
