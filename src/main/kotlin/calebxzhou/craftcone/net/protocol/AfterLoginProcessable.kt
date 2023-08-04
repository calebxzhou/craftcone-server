package calebxzhou.craftcone.net.protocol

import calebxzhou.craftcone.server.entity.ConePlayer

/**
 * Created  on 2023-07-21,22:21.
 */
//玩家登录后 数据包
interface AfterLoginProcessable {
    //处理数据
    fun process(player: ConePlayer)
}