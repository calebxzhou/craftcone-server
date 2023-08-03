package calebxzhou.craftcone.server.entity

import calebxzhou.craftcone.misc.InetSocketAddressSerializer
import calebxzhou.craftcone.misc.UuidSerializer
import calebxzhou.craftcone.server.DATA_DIR
import calebxzhou.craftcone.server.INFO_FILE
import calebxzhou.craftcone.server.logger
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.InetSocketAddress
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.Path

/**
 * Created  on 2023-07-18,21:02.
 */
@Serializable
data class ConePlayer(
    @Serializable(UuidSerializer::class)
    //玩家id
    val pid: UUID,
    //密码
    val pwd: String,
    @Serializable(InetSocketAddressSerializer::class)
    //ip地址
    val addr: InetSocketAddress
){
    //个人档案路径
    val profilePath = getProfilePath(pid)
    //正在游玩的房间
    val nowPlayingRoom
        get() = ConeRoom.addrToPlayingRoom[addr]
    //保存至文件
    fun saveToFile(){
        Files.writeString(profilePath.resolve(INFO_FILE), Json.encodeToString(this))
    }


    companion object{
        //全部在线玩家
        private val onlinePlayers = hashMapOf<UUID,ConePlayer>()
        //玩家ip to 玩家
        private val addrToPlayer = hashMapOf<InetSocketAddress, ConePlayer>()

        //玩家上线
        fun addOnlinePlayer(player: ConePlayer){
            onlinePlayers += Pair(player.pid,player)
            addrToPlayer += Pair(player.addr,player)
        }
        //验证密码是否正确
        fun validatePassword(pid:UUID,pwd: String) : Boolean{
            return readFromFile(pid).pwd == pwd
        }

        //登录
        fun login(player: ConePlayer):Boolean {
            return if(validatePassword(player.pid,player.pwd)){
                onlinePlayers += Pair(player.pid,player)
                true
            }else{
                false
            }
        }
        //根据ip地址获取在线玩家
        fun getByAddr(addr: InetSocketAddress): ConePlayer? {
            return addrToPlayer[addr]
        }

        //从文件读取玩家信息
        fun readFromFile(pid: UUID): ConePlayer{
            return Json.decodeFromString(Files.readString(getProfilePath(pid).resolve(INFO_FILE)))
        }
        //注册
        fun register(player: ConePlayer): Boolean {
            if(isRegistered(player)){
                logger.info { "${player.pid} 已注册过了，不允许再注册！" }
                return false;
            }
            Files.createDirectories(player.profilePath)
            player.saveToFile()
            logger.info { "$player 已注册" }
            return true
        }



        //个人档案路径
        fun getProfilePath(pid: UUID): Path {
            return Path("$DATA_DIR/players/$pid")
        }

        //是否注册过
        fun isRegistered(player: ConePlayer):Boolean{
            return isRegistered(player.pid)
        }
        fun isRegistered(pid: UUID): Boolean {
            return Files.exists(getProfilePath(pid))
        }

    }
}