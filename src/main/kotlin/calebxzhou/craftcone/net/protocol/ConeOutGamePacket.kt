package calebxzhou.craftcone.net.protocol

/**
 * Created  on 2023-06-29,20:43.
 */
//游戏内数据包
interface ConeOutGamePacket : ServerProcessablePacket,WritablePacket{
    companion object{
        const val PacketTypeNumber = 0
    }

}