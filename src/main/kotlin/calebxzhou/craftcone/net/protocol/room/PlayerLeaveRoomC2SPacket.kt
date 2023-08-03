package calebxzhou.craftcone.net.protocol.room

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.C2SPacket
import calebxzhou.craftcone.net.protocol.ReadablePacket
import java.net.InetSocketAddress

/**
 * Created  on 2023-07-06,8:48.
 */
class PlayerLeaveRoomC2SPacket (): C2SPacket {

    companion object : ReadablePacket<PlayerLeaveRoomC2SPacket>{
        override fun read(buf: FriendlyByteBuf): PlayerLeaveRoomC2SPacket {
            return PlayerLeaveRoomC2SPacket()
        }
    }

    override fun process(clientAddress: InetSocketAddress) {
        //找到玩家所在room，从playerList里移除他
    }


}