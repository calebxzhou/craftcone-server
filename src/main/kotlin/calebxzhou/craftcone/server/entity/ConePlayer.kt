package calebxzhou.craftcone.server.entity

import calebxzhou.craftcone.net.coneErrD
import calebxzhou.craftcone.net.coneInfoT
import calebxzhou.craftcone.net.coneSenP
import calebxzhou.craftcone.net.protocol.account.LoginByNameC2SPacket
import calebxzhou.craftcone.net.protocol.account.LoginByUidC2SPacket
import calebxzhou.craftcone.net.protocol.account.RegisterC2SPacket
import calebxzhou.craftcone.net.protocol.general.OkDataS2CPacket
import calebxzhou.craftcone.server.DB
import calebxzhou.craftcone.server.logger
import com.mongodb.client.model.Filters.eq
import kotlinx.coroutines.flow.firstOrNull
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.net.InetSocketAddress

/**
 * Created  on 2023-07-18,21:02.
 */
data class ConePlayer(
    @BsonId val id: ObjectId,
    //玩家名
    val name: String,
    //密码
    val pwd: String,
    val email: String,
    //创建时间
    val createTime: Long,
    //当前登录ip地址
    var addr: InetSocketAddress = InetSocketAddress(0)
) {
    override fun toString(): String {
        return "$name($id)"
    }

    companion object {
        private const val collectionName = "players"
        private val dbcl = DB.getCollection<ConePlayer>(collectionName)

        //全部在线玩家
        private val onlinePlayers = hashMapOf<ObjectId, ConePlayer>()

        //玩家ip to 玩家
        private val addrToPlayer = hashMapOf<InetSocketAddress, ConePlayer>()

        //根据ip地址获取在线玩家
        fun getOnlineByAddr(addr: InetSocketAddress): ConePlayer? = addrToPlayer[addr]

        //根据昵称获取
        suspend fun getByName(name: String): ConePlayer? = dbcl.find(eq(ConePlayer::name.name, name)).firstOrNull()

        //根据uid获取
        suspend fun getByUid(id: ObjectId): ConePlayer? = dbcl.find(eq(ConePlayer::id.name, id)).firstOrNull()

        //注册
        suspend fun register(clientAddress: InetSocketAddress, packet: RegisterC2SPacket) =
            getByName(packet.pName)?.run {
                coneErrD(clientAddress, "注册过了")
            } ?: run {
                ConePlayer(ObjectId(), packet.pName, packet.pwd, packet.email, System.currentTimeMillis()).let {
                    dbcl.insertOne(it)
                    logger.info { "$it 已注册" }
                    coneSenP(clientAddress, OkDataS2CPacket())
                }
            }

        suspend fun loginByUid(addr: InetSocketAddress, pkt: LoginByUidC2SPacket) =
            getByUid(pkt.uid)?.run {
                login(addr, pkt.pwd, this)
            } ?: coneErrD(addr, "未注册")

        suspend fun loginByName(addr: InetSocketAddress, pkt: LoginByNameC2SPacket) =
            getByName(pkt.name)?.run {
                login(addr, pkt.pwd, this)
            } ?: coneErrD(addr, "未注册")

        private fun login(addr: InetSocketAddress, pwd: String, player: ConePlayer) {
            if (player.pwd != pwd) {
                coneErrD(addr, "密码错误")
                return
            }
            player.addr = addr
            onlinePlayers += player.id to player
            addrToPlayer += player.addr to player
            logger.info { "$this 已上线！" }
            coneSenP(addr, OkDataS2CPacket())
            coneInfoT(addr, "登录成功")
        }

    }
}