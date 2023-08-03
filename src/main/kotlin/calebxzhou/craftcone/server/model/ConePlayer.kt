package calebxzhou.craftcone.server.model

import calebxzhou.craftcone.server.Consts.DATA_DIR
import java.net.InetSocketAddress
import java.nio.file.Path
import java.util.*
import kotlin.io.path.Path

/**
 * Created  on 2023-07-18,21:02.
 */
data class ConePlayer(
    val pid: UUID,
    val pwd: String,
    val addr: InetSocketAddress
){
    companion object{
        //全部在线玩家
        val onlinePlayers = arrayListOf<ConePlayer>()
        //玩家ip to 玩家
        private val addrToPlayer = hashMapOf<InetSocketAddress,ConePlayer>()

        //玩家上线
        fun addOnlinePlayer(player: ConePlayer){
            onlinePlayers += player
            addrToPlayer += Pair(player.addr,player)
        }

        //根据ip地址获取在线玩家
        fun getByAddr(addr: InetSocketAddress): ConePlayer? {
            return addrToPlayer[addr]
        }

        const val PWD_FILE = "password.dat"
        fun getProfilePath(pid: UUID): Path {
            return Path("$DATA_DIR/players/$pid")
        }

    }
}
