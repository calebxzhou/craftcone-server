package calebxzhou.craftcone.net.protocol

import calebxzhou.craftcone.server.entity.ConeOnlinePlayer

/**
 * Created  on 2023-07-21,22:21.
 */
//玩家登录后 数据包
interface AfterLoginProcessable {
    //处理数据
    suspend fun process(player: ConeOnlinePlayer)
}