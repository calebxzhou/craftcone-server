package calebxzhou.craftcone.net.protocol.game

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.C2SPacket
import calebxzhou.craftcone.net.protocol.ReadablePacket
import java.net.InetSocketAddress

/**
 * Created  on 2023-08-02,13:27.
 */
data class SetBlockStateC2SPacket(
    val id:Int,
    val bState:String
):C2SPacket {
    companion object : ReadablePacket<SetBlockStateC2SPacket>{
        override fun read(buf: FriendlyByteBuf): SetBlockStateC2SPacket {
            return SetBlockStateC2SPacket(buf.readVarInt(),buf.readUtf())
        }

    }

    override fun process(clientAddress: InetSocketAddress) {
        //获取所在room，保存方块注册表
    }

}