package calebxzhou.craftcone.net.protocol

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.account.*
import calebxzhou.craftcone.net.protocol.game.ChatC2CPacket
import calebxzhou.craftcone.net.protocol.game.PlayerMoveC2CPacket
import calebxzhou.craftcone.net.protocol.game.SetBlockC2CPacket
import calebxzhou.craftcone.net.protocol.room.PlayerJoinRoomC2SPacket
import calebxzhou.craftcone.net.protocol.room.PlayerLeaveRoomC2SPacket
import calebxzhou.craftcone.server.LOG
import java.net.InetSocketAddress

/**
 * Created  on 2023-07-14,8:55.
 */
object ConePacketSet {


    //包id和包类型对应map 全局通用
    private val idToPacketType = arrayListOf<PacketType>()

    //包class和包id对应map 全局通用
    private val classToPacketId = linkedMapOf<Class<out Packet>, Int>()

    /**C2S**/
    //包id和read方法对应map c2s读包用
    private val c2s_idToPacketReader = arrayListOf<(FriendlyByteBuf) -> C2SPacket>()
    private fun registerC2SPacket(reader: (FriendlyByteBuf) -> C2SPacket) {
        idToPacketType += PacketType.C2S
        c2s_idToPacketReader += reader
    }

    /**S2C**/
    private fun registerS2CPacket(packetClass: Class<out S2CPacket>) {
        idToPacketType += PacketType.S2C
        classToPacketId += Pair(packetClass, idToPacketType.size)
    }

    /**C2C**/
    private val c2c_idToPacketReader = arrayListOf<(FriendlyByteBuf) -> C2CPacket>()

    //idToPacketClassReader
    private fun registerC2CPacket(packetClass: Class<out C2CPacket>, reader: (FriendlyByteBuf) -> C2CPacket) {
        idToPacketType += PacketType.C2C
        classToPacketId += Pair(packetClass, idToPacketType.size)
        c2c_idToPacketReader += reader
    }

    fun createPacketAndProcess(packetId: Int, clientAddr: InetSocketAddress, data: FriendlyByteBuf) {
        val packet = try {
            when (idToPacketType[packetId]) {
                PacketType.C2S -> {
                    c2s_idToPacketReader[packetId].invoke(data)
                }

                PacketType.C2C -> {
                    c2c_idToPacketReader[packetId].invoke(data)
                }

                else -> {
                    LOG.error("服务器不应该收到S2C数据包（ID=$packetId）")
                    return
                }
            }
        } catch (e: ArrayIndexOutOfBoundsException) {
            LOG.error("找不到包ID$packetId")
            return
        }
        packet.process(clientAddr)
    }

    fun getPacketId(packetClass: Class<out Packet>): Int? {
        return classToPacketId[packetClass]
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
        registerC2CPacket(PlayerMoveC2CPacket::class.java, PlayerMoveC2CPacket::read)
        registerC2CPacket(SetBlockC2CPacket::class.java, SetBlockC2CPacket::read)

    }

}