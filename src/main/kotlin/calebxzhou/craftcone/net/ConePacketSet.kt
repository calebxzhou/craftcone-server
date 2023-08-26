package calebxzhou.craftcone.net

import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.net.protocol.account.LoginC2SPacket
import calebxzhou.craftcone.net.protocol.account.RegisterC2SPacket
import calebxzhou.craftcone.net.protocol.game.*
import calebxzhou.craftcone.net.protocol.general.DisconnectS2CPacket
import calebxzhou.craftcone.net.protocol.general.OkDataS2CPacket
import calebxzhou.craftcone.net.protocol.general.SysMsgS2CPacket
import calebxzhou.craftcone.net.protocol.room.*
import calebxzhou.craftcone.server.entity.ConeRoom
import calebxzhou.craftcone.server.logger
import java.net.InetSocketAddress

/**
 * Created  on 2023-07-14,8:55.
 */
object ConePacketSet {
    enum class PacketType {
        READ,WRITE
    }






    //包writer/reader种类 [id]
    private val packetTypes = arrayListOf<PacketType>()
    //c2s
    private val packetIdReaders = linkedMapOf<Int,(FriendlyByteBuf) -> Packet>()
    //s2c
    private val packetWriterClassIds = linkedMapOf<Class<out BufferWritable>,Int>()
    init {
        registerPacket(ConeRoom::class.java)
        registerPacket(DisconnectS2CPacket::class.java)
        registerPacket(OkDataS2CPacket::class.java)
        registerPacket(SysMsgS2CPacket::class.java)

        registerPacket(LoginC2SPacket::read)
        registerPacket(RegisterC2SPacket::read)

        registerPacket(BlockDataC2CPacket::class.java)
        registerPacket(BlockDataC2CPacket::read)
        registerPacket(GetChunkC2SPacket::read)
        registerPacket(MovePlayerWpC2SPacket::read)
        registerPacket(MovePlayerXyzC2SPacket::read)
        registerPacket(PlayerJoinedRoomS2CPacket::class.java)
        registerPacket(PlayerLeftRoomS2CPacket::class.java)
        registerPacket(PlayerMoveWpS2CPacket::class.java)
        registerPacket(PlayerMoveXyzS2CPacket::class.java)
        registerPacket(SendChatMsgC2SPacket::read)

        registerPacket(CreateRoomC2SPacket::read)
        registerPacket(DelRoomC2SPacket::read)
        registerPacket(GetRoomC2SPacket::read)
        registerPacket(JoinRoomC2SPacket::read)
        registerPacket(LeaveRoomC2SPacket::read)

    }

    private fun registerPacket(reader: (FriendlyByteBuf) -> Packet){
        packetIdReaders += Pair (packetTypes.size,reader)
        packetTypes += PacketType.READ
    }
    private fun registerPacket(writerClass: Class<out BufferWritable>) {
        packetWriterClassIds += Pair (writerClass, packetTypes.size)
        packetTypes += PacketType.WRITE
    }
    
    //客户端传入包 服务端这边创建+处理
    fun createAndProcess(clientAddr: InetSocketAddress, packetId: Int,  data: FriendlyByteBuf){
        val type = packetTypes.getOrNull(packetId)?:let {
            logger.error { "找不到ID$packetId 的包" }
            return
        }
        when(type){
            PacketType.READ ->{
                val packet = packetIdReaders[packetId] ?.invoke(data)?:let{
                    logger.error { "找不到ID$packetId 的包" }
                    return
                }
                ConePacketProcessor.processPacket(clientAddr,packet)
            }
            else -> {
                logger.error { "$clientAddr 客户端只能传入c2s包 ID$packetId 不是c2s包" }
                return
            }
        }

    }

    fun getPacketId(packetClass: Class<out BufferWritable>): Int? {
        return packetWriterClassIds[packetClass]
    }




}