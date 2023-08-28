package calebxzhou.craftcone.net.protocol

import java.net.InetSocketAddress

/**
 * Created  on 2023-07-21,22:21.
 */
//玩家登录前 数据包
interface BeforeLoginProcessable {
    //处理数据
    suspend fun process(clientAddress: InetSocketAddress)
}