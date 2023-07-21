package calebxzhou.craftcone.net.protocol

import calebxzhou.craftcone.net.FriendlyByteBuf

/**
 * Created  on 2023-06-29,20:43.
 */
//server -> client包
interface S2CPacket  {
    //写数据进FriendlyByteBuf
    fun write(buf: FriendlyByteBuf)
}