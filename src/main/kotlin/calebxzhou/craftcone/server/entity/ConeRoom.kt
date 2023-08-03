package calebxzhou.craftcone.server.entity

import calebxzhou.craftcone.server.Consts.DATA_DIR
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.Path

/**
 * Created  on 2023-08-03,13:20.
 */
data class ConeRoom(
    //房间ID
    val rid: UUID,
    //房间名
    val rName: String,
    //房主id
    val ownerUid: UUID,
    //mod加载器？Fabric：Forge
    val isFabric: Boolean,
    //创造
    val isCreative: Boolean,
    //方块状态数量
    val blockStateAmount: Int,
    //地图种子
    val seed: Long,

) {
    //房间档案path
    val profilePath = getProfilePath(rid)
    //正在游玩 当前房间的 玩家list
    val players = arrayListOf<ConePlayer>()
    companion object{
        fun getProfilePath(rid:UUID): Path {
            return  Path("$DATA_DIR/rooms/$rid")
        }
        fun exists(rid: UUID) :Boolean{
            return Files.exists(getProfilePath(rid))
        }
    }
}