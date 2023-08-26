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
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
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
        fun exists(name: String): Boolean = transaction {  !PlayerInfoTable.select { PlayerInfoTable.name eq name }.empty()}

        //注册
        fun register(pwd: String, pName: String, clientAddress: InetSocketAddress) {
            if (exists(pName)) {
                coneErrD(clientAddress, "注册过了")
                return
            }
            val player = ConePlayer(0, pName, pwd, System.currentTimeMillis())
            logger.info { "$player 已注册" }

            coneSenP(clientAddress, OkDataS2CPacket{it.writeVarInt(create(player))})
        }

        //通过id读取玩家信息，找不到返回null
        fun read(pid: Int): ConePlayer? {
            return transaction {
                PlayerInfoRow.findById(pid)?.run {
                    ConePlayer(id.value, name, pwd, createTime)
                }
            }
        }
        //新增玩家
        fun create(player: ConePlayer):Int{
            return transaction {
                PlayerInfoRow.new {
                    name = player.name
                    pwd = player.pwd
                    createTime = player.createTime
                }.id.value
            }
        }

        //登录
        fun login(pid: Int, pwd: String, addr: InetSocketAddress) {
            /*if(onlinePlayers.containsKey(pid)){
                coneErrD(addr,"已经在线了")
                return
            }*/
            read(pid)?.apply{
                if(this.pwd != pwd){
                    return@apply
                }
                this.addr = addr
                onlinePlayers += this.id to this
                addrToPlayer += this.addr to this
                logger.info { "$this 已上线！" }

                coneSenP(addr, OkDataS2CPacket(null))
                coneInfoT(addr,"欢迎来到服务器！")
            } ?: coneErrD(addr, "用户UID和密码不匹配")
        }


        //根据ip地址获取在线玩家
        fun getByAddr(addr: InetSocketAddress): ConePlayer? {
            return addrToPlayer[addr]
        }


    }
}