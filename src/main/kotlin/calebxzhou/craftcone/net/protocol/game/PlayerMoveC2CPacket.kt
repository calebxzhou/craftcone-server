package calebxzhou.craftcone.net.protocol.game

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.BufferReadable
import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.InRoomProcessable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.server.entity.Player
import calebxzhou.craftcone.server.entity.Room

/**
 * Created  on 2023-07-13,10:21.
 */
data class PlayerMoveC2CPacket(
    val pid:Int,
    val x:Float,
    val y:Float,
    val z:Float,
    val w:Float,
    val p:Float,
) : Packet, InRoomProcessable, BufferWritable {
    companion object : BufferReadable<PlayerMoveC2CPacket>{
        override fun read(buf: FriendlyByteBuf): PlayerMoveC2CPacket {
            return PlayerMoveC2CPacket(buf.readVarInt(),buf.readFloat(),buf.readFloat(),buf.readFloat(),buf.readFloat(),buf.readFloat())
        }
    }

    override fun process(player: Player, playingRoom: Room) {
        playingRoom.broadcastPacket(this,player)
    }

    override fun write(buf: FriendlyByteBuf) {
        buf.writeVarInt(pid)
        buf.writeFloat(x)
        buf.writeFloat(y)
        buf.writeFloat(z)
        buf.writeFloat(w)
        buf.writeFloat(p)
    }




}