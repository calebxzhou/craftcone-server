package calebxzhou.craftcone.server.entity

import calebxzhou.craftcone.net.coneErrDialog
import calebxzhou.craftcone.net.coneInfoToast
import calebxzhou.craftcone.net.coneSendPacket
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.net.protocol.account.LoginByNameC2SPacket
import calebxzhou.craftcone.net.protocol.account.LoginByUidC2SPacket
import calebxzhou.craftcone.net.protocol.account.RegisterC2SPacket
import calebxzhou.craftcone.net.protocol.general.OkDataS2CPacket
import calebxzhou.craftcone.server.DB
import calebxzhou.craftcone.server.logger
import calebxzhou.craftcone.util.ByteBufUt.writeObjectId
import com.mongodb.client.model.Filters.eq
import io.netty.channel.ChannelHandlerContext
import kotlinx.coroutines.flow.firstOrNull
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

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
) : Packet {

    override fun toString(): String {
        return "$name($id)"
    }

    companion object {
        const val collectionName = "players"
        private val dbcl = DB.getCollection<ConePlayer>(collectionName)




        //根据昵称获取
        suspend fun getByName(name: String): ConePlayer? = dbcl.find(eq(ConePlayer::name.name, name)).firstOrNull()

        //根据uid获取
        suspend fun getByUid(id: ObjectId): ConePlayer? = dbcl.find(eq("_id", id)).firstOrNull()

        //注册
        suspend fun register(ctx: ChannelHandlerContext, packet: RegisterC2SPacket) =
            getByName(packet.pName)?.run {
                coneErrDialog(ctx, "注册过了")
            } ?: run {
                ConePlayer(ObjectId(), packet.pName, packet.pwd, packet.email, System.currentTimeMillis()).run {
                    dbcl.insertOne(this)
                    coneSendPacket(ctx, OkDataS2CPacket{it.writeObjectId(id)})
                }
            }

        suspend fun loginByUid(ctx: ChannelHandlerContext, pkt: LoginByUidC2SPacket) =
            getByUid(pkt.uid)?.run {
                login(ctx, pkt.pwd, this)
            } ?: coneErrDialog(ctx, "未注册")

        suspend fun loginByName(ctx: ChannelHandlerContext, pkt: LoginByNameC2SPacket) =
            getByName(pkt.name)?.run {
                login(ctx, pkt.pwd, this)
            } ?: coneErrDialog(ctx, "未注册")

        private fun login(ctx: ChannelHandlerContext, pwd: String, player: ConePlayer) {
            if (player.pwd != pwd) {
                coneErrDialog(ctx, "密码错误")
                return
            }
            logger.info { "$player 已登录" }
            ConeOnlinePlayer.goOnline(ConeOnlinePlayer(player,ctx))
            coneSendPacket(ctx, OkDataS2CPacket{it.writeObjectId(player.id)})
            coneInfoToast(ctx, "登录成功")
        }

    }
}