package calebxzhou.craftcone.server.entity

import calebxzhou.craftcone.misc.InetSocketAddressSerializer
import calebxzhou.craftcone.misc.UuidSerializer
import calebxzhou.craftcone.server.Consts.DATA_DIR
import kotlinx.serialization.Serializable
import java.net.InetSocketAddress
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
    val profilePath = Path("$DATA_DIR/players/$pid")
    //正在游玩的房间
    val nowPlayingRoom
        get() = ConeRoom.addrToPlayingRoom[addr]
}