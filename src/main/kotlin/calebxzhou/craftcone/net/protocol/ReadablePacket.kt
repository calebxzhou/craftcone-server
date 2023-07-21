package calebxzhou.craftcone.net.protocol

import calebxzhou.craftcone.net.FriendlyByteBuf


/**
 * Created  on 2023-07-13,17:27.
 */
//通用数据包
interface ReadablePacket : Packet{
    // 读取 数据
    fun read(buf: FriendlyByteBuf) : Any
}