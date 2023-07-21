package calebxzhou.craftcone.net.protocol

import java.net.InetSocketAddress

/**
 * Created  on 2023-07-21,22:21.
 */
interface ServerProcessablePacket {
    //处理数据
    fun process(clientAddress: InetSocketAddress)
}