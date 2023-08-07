package calebxzhou.craftcone.server.entity

import calebxzhou.craftcone.misc.UuidSerializer
import calebxzhou.craftcone.net.ConeNetSender
import calebxzhou.craftcone.server.logger
import calebxzhou.craftcone.server.table.PlayerInfoRow
import calebxzhou.craftcone.server.table.PlayerInfoTable
import calebxzhou.craftcone.server.table.RoomInfoRow
import calebxzhou.craftcone.server.table.RoomInfoTable
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import java.net.InetSocketAddress
import java.util.*

/**
 * Created  on 2023-07-18,21:02.
 */
@Serializable
data class Player(
    @Serializable(UuidSerializer::class)
    //玩家id
    val id: UUID,
    //玩家名
    val name: String,
    //密码
    val pwd: String,
    //创建时间
    val createTime: Long,
    @Transient
    //当前登录ip地址
    var addr: InetSocketAddress = InetSocketAddress(0)
) {

    //正在游玩的房间
    var nowPlayingRoom: Room? = null

    //保存
    fun write() {
        PlayerInfoRow.new(id) {
            name = this@Player.name
            pwd = this@Player.pwd
            createTime = this@Player.createTime
        }
    }

    //加入房间
    fun joinRoom(rid: UUID): Boolean {
        val room = if(!Room.isRunning(rid)){
            Room.read(rid)?.also {
                it.start()
            }?:let {
                logger.warn { "$this 请求加入不存在的房间 $rid" }
                return false
            }
        }else{
            Room.getRunning(rid)?:let {
                logger.warn { "$this 请求加入未运行的房间 $rid" }
                return false
            }
        }
        ConeNetSender.sendPacket(room.infoPacket, this)
        room.playerJoin(this)
        this.nowPlayingRoom = room
        logger.info { "$this 加入了房间 $room" }
        return true
    }

    //离开房间
    fun leaveRoom() {
        val room = nowPlayingRoom ?: let {
            logger.info { "$this 未加入任何房间就请求离开" }
            return
        }
        logger.info { "$this 已离开房间 $room" }
        nowPlayingRoom = null
        room.playerLeave(this)

    }
    //已创建房间数量
    val ownRoomAmount: Int
        get() = RoomInfoRow.find { RoomInfoTable.ownerId eq id }.count().toInt()
    override fun toString(): String {
        return "$name($id)"
    }


    companion object {
        //全部在线玩家
        private val onlinePlayers = hashMapOf<UUID, Player>()

        //玩家ip to 玩家
        private val addrToPlayer = hashMapOf<InetSocketAddress, Player>()

        //是否注册过
        fun exists(pid: UUID): Boolean {
            return !PlayerInfoTable.select { PlayerInfoTable.id eq pid }.empty()

        }

        //注册
        fun create(pid: UUID, pwd: String, pName: String): Boolean {
            if (exists(pid)) {
                logger.info { "$pName 已注册过了，不允许再注册！" }
                return false;
            }
            val player = Player(pid, pName, pwd, System.currentTimeMillis())
            player.write()
            logger.info { "$player 已注册" }
            return true
        }

        //读取玩家信息
        fun readFromRow(row: PlayerInfoRow): Player {
            return Player(row.id.value, row.name, row.pwd, row.createTime)
        }

        //读取玩家信息
        fun read(pid: UUID): Player? {
            return readFromRow(PlayerInfoRow.findById(pid)?:return null)
        }

        //登录
        fun login(pid: UUID, pwd: String, addr: InetSocketAddress): Boolean {
            val find = PlayerInfoRow.find {
                (PlayerInfoTable.id eq pid) and (PlayerInfoTable.pwd eq pwd)
            }
            val pwdCorrect = find.count()>0
            return if (pwdCorrect) {
                val player = readFromRow(find.first())
                player.addr = addr

                onlinePlayers += Pair(player.id, player)
                addrToPlayer += Pair(player.addr, player)
                logger.info { "$player 已上线！" }

                true
            } else {
                false
            }
        }


        //根据ip地址获取在线玩家
        fun getByAddr(addr: InetSocketAddress): Player? {
            return addrToPlayer[addr]
        }







    }
}