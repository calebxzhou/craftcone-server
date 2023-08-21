package calebxzhou.craftcone.server.entity

import calebxzhou.craftcone.net.ConeNetSender
import calebxzhou.craftcone.net.coneErrD
import calebxzhou.craftcone.net.coneInfoT
import calebxzhou.craftcone.net.coneSenP
import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.general.OkDataS2CPacket
import calebxzhou.craftcone.server.logger
import calebxzhou.craftcone.server.table.PlayerInfoRow
import calebxzhou.craftcone.server.table.PlayerInfoTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import java.net.InetSocketAddress

/**
 * Created  on 2023-07-18,21:02.
 */
data class ConePlayer(
    //玩家id
    val id: Int,
    //玩家名
    val name: String,
    //密码
    val pwd: String,
    //创建时间
    val createTime: Long,
    //当前登录ip地址
    var addr: InetSocketAddress = InetSocketAddress(0)
) {

    //保存，返回ID
    fun insert(): Int {
        return PlayerInfoRow.new {
            name = this@ConePlayer.name
            pwd = this@ConePlayer.pwd
            createTime = this@ConePlayer.createTime
        }.id.value

    }

    override fun toString(): String {
        return "$name($id)"
    }

    fun sendPacket(packet: BufferWritable) {
        ConeNetSender.sendPacket(packet, this)
    }


    companion object {
        //全部在线玩家
        private val onlinePlayers = hashMapOf<Int, ConePlayer>()

        //玩家ip to 玩家
        private val addrToPlayer = hashMapOf<InetSocketAddress, ConePlayer>()

        //用户名是否存在
        fun exists(name: String): Boolean {
            return !PlayerInfoTable.select { PlayerInfoTable.name eq name }.empty()
        }

        //注册
        fun register(pwd: String, pName: String, clientAddress: InetSocketAddress) {
            if (exists(pName)) {
                coneErrD(clientAddress, "注册过了")
                return
            }
            val player = ConePlayer(0, pName, pwd, System.currentTimeMillis())
            logger.info { "$player 已注册" }

            coneSenP(clientAddress, OkDataS2CPacket{it.writeVarInt(player.insert())})
        }

        //读取玩家信息
        fun readFromRow(row: PlayerInfoRow): ConePlayer {
            return ConePlayer(row.id.value, row.name, row.pwd, row.createTime)
        }

        //读取玩家信息
        fun read(pid: Int): ConePlayer? {
            return readFromRow(PlayerInfoRow.findById(pid) ?: return null)
        }

        //登录
        fun login(pid: Int, pwd: String, addr: InetSocketAddress) {
            val find = PlayerInfoRow.find {
                (PlayerInfoTable.id eq pid) and (PlayerInfoTable.pwd eq pwd)
            }
            val pwdCorrect = find.count() > 0
            return if (pwdCorrect) {
                val player = readFromRow(find.first())
                player.addr = addr

                onlinePlayers += Pair(player.id, player)
                addrToPlayer += Pair(player.addr, player)
                logger.info { "$player 已上线！" }

                coneSenP(addr, OkDataS2CPacket(null))
                coneInfoT(addr,"欢迎来到服务器！")
            } else {
                coneErrD(addr, "用户UID和密码不匹配")
            }
        }


        //根据ip地址获取在线玩家
        fun getByAddr(addr: InetSocketAddress): ConePlayer? {
            return addrToPlayer[addr]
        }


    }
}