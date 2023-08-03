package calebxzhou.craftcone.server.entity

import calebxzhou.craftcone.server.Consts.DATA_DIR
import calebxzhou.craftcone.server.RoomManager
import java.net.InetSocketAddress
import java.util.*
import kotlin.io.path.Path

/**
 * Created  on 2023-07-18,21:02.
 */
data class ConePlayer(
    //玩家id
    val pid: UUID,
    //密码
    val pwd: String,
    //ip地址
    val addr: InetSocketAddress
){
    //个人档案路径
    val profilePath = Path("$DATA_DIR/players/$pid")
    //正在游玩的房间
    val nowPlayingRoom
        get() = RoomManager.addrToPlayingRoom[addr]
}