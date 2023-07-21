package calebxzhou.craftcone.net.protocol

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.game.ChatC2CPacket
import calebxzhou.craftcone.net.protocol.room.PlayerJoinRoomC2SPacket
import calebxzhou.craftcone.net.protocol.room.PlayerLeaveRoomC2SPacket
import calebxzhou.craftcone.net.protocol.game.SetBlockC2CPacket

/**
 * Created  on 2023-07-14,8:55.
 */
object ConePacketSet {
    fun getPacketId(packetClass: Class<out InGamePacket>): Int? {
        return classToId[packetClass]
    }

    fun createPacket(packetId: Int, data: FriendlyByteBuf): InGamePacket {
        return idToReader[packetId].invoke(data)
    }

    private val classToId = linkedMapOf<Class<out InGamePacket>, Int>()
    private val idToReader = arrayListOf<(FriendlyByteBuf) -> InGamePacket>()

    init {
        addPackets(
            Pair(SetBlockC2CPacket::class.java, SetBlockC2CPacket::read),
            Pair(ChatC2CPacket::class.java, ChatC2CPacket::read),
            Pair(PlayerJoinRoomC2SPacket::class.java, PlayerJoinRoomC2SPacket::read),
            Pair(PlayerLeaveRoomC2SPacket::class.java, PlayerLeaveRoomC2SPacket::read),
        )
    }

    private fun addPackets(vararg packetClassAndReader: Pair<Class<out InGamePacket>, (FriendlyByteBuf) -> InGamePacket>) {
        packetClassAndReader.forEach {
            val packetClass = it.first
            val packetReader = it.second
            addPacket(packetClass, packetReader)
        }
    }

    private fun addPacket(packetClass: Class<out InGamePacket>, packetReader: (FriendlyByteBuf) -> InGamePacket) {
        val size = idToReader.size
        classToId[packetClass] = size
        idToReader += packetReader
    }

}