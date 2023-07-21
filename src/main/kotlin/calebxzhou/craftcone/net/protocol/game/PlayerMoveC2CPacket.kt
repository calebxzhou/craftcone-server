package calebxzhou.craftcone.net.protocol.game

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.C2CPacket
import calebxzhou.craftcone.net.protocol.ReadablePacket

/**
 * Created  on 2023-07-13,10:21.
 */
data class PlayerMoveC2CPacket(
    val tempPid: Int,
    val x:Float,
    val y:Float,
    val z:Float,
) : C2CPacket {
    companion object : ReadablePacket{
        override fun read(buf: FriendlyByteBuf): PlayerMoveC2CPacket {
            return PlayerMoveC2CPacket(buf.readVarInt(),buf.readFloat(),buf.readFloat(),buf.readFloat())
        }
    }
    override fun write(buf: FriendlyByteBuf) {
        buf.writeVarInt(tempPid)
        buf.writeFloat(x)
        buf.writeFloat(y)
        buf.writeFloat(z)
    }



}