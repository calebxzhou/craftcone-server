package calebxzhou.craftcone.server.model

import java.util.*

/**
 * Created  on 2023-08-03,13:20.
 */
data class ConeRoom(
    //房间ID
    val rid: UUID,
    //房间名
    val rName: String,
    //房主
    val ownerUid: UUID,
    //mod加载器？Fabric：Forge
    val isFabric: Boolean,
    //创造
    val isCreative: Boolean,
    //方块状态数量
    val blockStateAmount: Int,
    //地图种子
    val seed: Long,
    //当前房间的所有在线玩家
    val players: List<ConePlayer>
) {
    companion object{
        //所有正在游玩的房间 (ID to Room)
        val onlineRooms = hashMapOf<UUID,ConeRoom>()
    }
}