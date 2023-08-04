package calebxzhou.craftcone.server.entity

import calebxzhou.craftcone.misc.UuidSerializer
import calebxzhou.craftcone.net.ConeNetManager
import calebxzhou.craftcone.server.DATA_DIR
import calebxzhou.craftcone.server.INFO_FILE
import calebxzhou.craftcone.server.logger
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
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
    //玩家名
    val pName:String,
    //密码
    val pwd: String,
    //创建时间
    val createTime: Long,
    @Transient
    //当前登录ip地址
    var addr: InetSocketAddress = InetSocketAddress(0)
){
    //创建了几个房间
    val createdRoomAmount: Int
        get() = ownRoomPath.toFile().listFiles()?.size?:0

    @Transient
    //个人档案路径
    val profilePath = getProfilePath(pid)
    val ownRoomPath = profilePath.resolve("own_rooms/")
    //正在游玩的房间
    var nowPlayingRoom : ConeRoom? = null

    //保存至文件
    fun write(){
        Files.createDirectories(profilePath)
        Files.writeString(profilePath.resolve(INFO_FILE), Json.encodeToString(this))
    }

    //离开房间
    fun leaveRoom() {
        if(nowPlayingRoom!=null){
            logger.info { "$this 离开房间 $nowPlayingRoom" }
            nowPlayingRoom?.players?.remove(pid)
            nowPlayingRoom=null
        }

    }

    override fun toString(): String {
        return  "$pName($pid)"
    }

    fun joinRoom(rid: UUID):Boolean {
        //房间是否在线
        if (ConeRoom.isOnline(rid)){
            //在线则直接加入
            val room = ConeRoom.playerJoinRoom(this, rid)
            if(room != null){
                ConeNetManager.sendPacket(room.infoPacket,this)
                this.nowPlayingRoom = room
                return true
            }
        }else{
            //离线则查询此房间是否存在
            if(ConeRoom.exists(rid)){
                //存在则载入房间
                ConeRoom.load(rid)
                //然后再加入
                val room  = ConeRoom.playerJoinRoom(this, rid)?:let {
                    logger.warn { "房间$rid 不存在" }
                    return false
                }
                ConeNetManager.sendPacket(room.infoPacket,this)
                this.nowPlayingRoom = room
                return true
            }else{
                //不存在提示失败
                logger.warn { "房间$rid 不存在" }
                return false
            }
        }
        return false
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
        fun login(pid:UUID,pwd: String,addr: InetSocketAddress):Boolean {
            return if(validatePassword(pid,pwd)){
                val player = readFromFile(pid)
                player.addr = addr
                onlinePlayers += Pair(player.pid,player)
                addrToPlayer += Pair(addr,player)
                logger.info { "$player 已登录！" }
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
        fun register(pid: UUID, pwd: String, pName: String): Boolean {
            if(isRegistered(pid)){
                logger.info { "$pName 已注册过了，不允许再注册！" }
                return false;
            }
            val player = ConePlayer(pid, pName, pwd, System.currentTimeMillis())
            player.write()
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