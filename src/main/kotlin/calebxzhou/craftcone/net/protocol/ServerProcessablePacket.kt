package calebxzhou.craftcone.net.protocol

import java.net.InetSocketAddress

/**
 * Created  on 2023-07-13,20:33.
 */
interface ServerProcessablePacket {
    //处理包
    fun process(clientAddress: InetSocketAddress)
}