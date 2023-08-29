package calebxzhou.craftcone.net.protocol

import calebxzhou.craftcone.net.ConeByteBuf


/**
 * Created  on 2023-07-13,17:27.
 */
//数据可读包
interface BufferReadable<T:Packet> : Packet{
    // 读取 数据
    fun read(buf: ConeByteBuf) : T
}