package calebxzhou.craftcone.server

import calebxzhou.craftcone.server.entity.ConePlayer
import java.net.InetSocketAddress
import java.nio.file.Files
import java.util.*

/**
 * Created  on 2023-08-03,14:37.
 */
object PlayerManager {
    //全部在线玩家
    private val onlinePlayers = hashMapOf<UUID,ConePlayer>()
    //玩家ip to 玩家
    private val addrToPlayer = hashMapOf<InetSocketAddress, ConePlayer>()
    const val PWD_FILE = "password.dat"
    //玩家上线
    fun addOnlinePlayer(player: ConePlayer){
        onlinePlayers += Pair(player.pid,player)
        addrToPlayer += Pair(player.addr,player)
    }

    //根据ip地址获取在线玩家
    fun getByAddr(addr: InetSocketAddress): ConePlayer? {
        return addrToPlayer[addr]
    }
    //是否注册过
    fun isRegistered(player: ConePlayer): Boolean {
        return Files.exists(player.profilePath)
    }


    //注册
    fun register(player: ConePlayer): Boolean {
        if(isRegistered(player)){
            return false;
        }
        Files.createDirectories(player.profilePath)
        Files.writeString(player.profilePath.resolve(PWD_FILE),player.pwd)
        LOG.info { "$player 已注册" }
        return true
    }

    fun validatePassword(player: ConePlayer) : Boolean{
        val savedPwd = Files.readString(player.profilePath.resolve(PWD_FILE))
        return player.pwd == savedPwd
    }
}