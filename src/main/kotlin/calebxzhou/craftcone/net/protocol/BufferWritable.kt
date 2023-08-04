package calebxzhou.craftcone.net.protocol

import calebxzhou.craftcone.net.FriendlyByteBuf

/**
 * Created  on 2023-07-21,22:22.
 */
interface BufferWritable{
    //写数据进FriendlyByteBuf
    fun write(buf: FriendlyByteBuf)
}