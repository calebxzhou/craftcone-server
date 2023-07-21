package calebxzhou.craftcone.net.protocol

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.account.*
import calebxzhou.craftcone.net.protocol.game.ChatC2CPacket
import calebxzhou.craftcone.net.protocol.game.PlayerMoveC2CPacket
import calebxzhou.craftcone.net.protocol.game.SetBlockC2CPacket
import calebxzhou.craftcone.net.protocol.room.PlayerJoinRoomC2SPacket
import calebxzhou.craftcone.net.protocol.room.PlayerLeaveRoomC2SPacket

/**
 * Created  on 2023-07-14,8:55.
 */
object ConePacketSet {
    /**C2S**/

    //包id和包类型对应map 全局通用
    private val idToPacketType = arrayListOf<PacketType>()
    //包id和read方法对应map c2s读包用
    private val c2s_idToPacketReader =  arrayListOf<(FriendlyByteBuf) -> C2SPacket>()
    private fun registerC2SPacket(reader: (FriendlyByteBuf) -> C2SPacket){
        idToPacketType += PacketType.C2S
        c2s_idToPacketReader += reader
    }

    /**S2C**/
    //包class和id对应map（s2c发包用）
    private val s2c_packetClassToId = linkedMapOf<Class<out S2CPacket>, Int>()
    private fun registerS2CPacket(packetClass: Class<out S2CPacket>){
        idToPacketType += PacketType.S2C
        s2c_packetClassToId += Pair(packetClass,idToPacketType.size)
    }

    /**C2C**/
    private val c2c_packetClassToId = linkedMapOf<Class<out C2CPacket>, Int>()
    private val c2c_idToPacketReader = linkedMapOf<Int, (FriendlyByteBuf) -> C2CPacket>()
    //idToPacketClassReader
    private fun registerC2CPacket(packetClass: Class<out C2CPacket>, reader: (FriendlyByteBuf) -> C2CPacket){
        idToPacketType += PacketType.C2C
        c2c_packetClassToId += Pair(packetClass, idToPacketType.size)
        c2c_idToPacketReader += Pair(idToPacketType.size,reader)
    }
    init {
        registerC2SPacket(CheckPlayerExistC2SPacket::read)
        registerS2CPacket(CheckPlayerExistS2CPacket::class.java)

        registerC2SPacket(LoginC2SPacket::read)
        registerS2CPacket(LoginS2CPacket::class.java)

        registerC2SPacket(RegisterC2SPacket::read)
        registerS2CPacket(RegisterS2CPacket::class.java)

        registerC2SPacket(PlayerJoinRoomC2SPacket::read)

        registerC2SPacket(PlayerLeaveRoomC2SPacket::read)

        registerS2CPacket(CheckPlayerExistS2CPacket::class.java)
        registerS2CPacket(CheckPlayerExistS2CPacket::class.java)

        registerC2CPacket(ChatC2CPacket::class.java, ChatC2CPacket::read)
        registerC2CPacket(PlayerMoveC2CPacket::class.java,PlayerMoveC2CPacket::read)
        registerC2CPacket(SetBlockC2CPacket::class.java,SetBlockC2CPacket::read)

    }

}