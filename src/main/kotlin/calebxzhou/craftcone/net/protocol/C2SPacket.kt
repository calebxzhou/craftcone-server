package calebxzhou.craftcone.net.protocol

import java.net.InetSocketAddress

/**
 * Created  on 2023-06-29,20:43.
 */
//client -> server包
interface C2SPacket : Packet{
    //处理数据
    fun process(clientAddress: InetSocketAddress)
}