package calebxzhou.craftcone.net

import calebxzhou.craftcone.net.protocol.*
import calebxzhou.craftcone.net.protocol.account.*
import calebxzhou.craftcone.net.protocol.game.*
import calebxzhou.craftcone.net.protocol.room.*
import calebxzhou.craftcone.server.entity.Player
import calebxzhou.craftcone.server.logger
import org.jetbrains.exposed.sql.transactions.transaction
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
        registerPacket(CheckPlayerExistC2SPacket::read)
        registerPacket(CheckPlayerExistS2CPacket::class.java)
        registerPacket(LoginC2SPacket::read)
        registerPacket(LoginS2CPacket::class.java)
        registerPacket(RegisterC2SPacket::read)
        registerPacket(RegisterS2CPacket::class.java)

        registerPacket(ChatC2CPacket::read)
        registerPacket(ChatC2CPacket::class.java)
        registerPacket(PlayerMoveC2CPacket::read)
        registerPacket(PlayerMoveC2CPacket::class.java)
        registerPacket(ReadBlockC2SPacket::read)
        registerPacket(ReadBlockS2CPacket::class.java)
        registerPacket(WriteBlockC2SPacket::read)
        registerPacket(SetBlockC2CPacket::read)
        registerPacket(SetBlockC2CPacket::class.java)
        //registerC2SPacket(SetBlockStateC2SPacket::read)
        registerPacket(SysChatMsgS2CPacket::class.java)
        registerPacket(PlayerCreateRoomC2SPacket::read)
        registerPacket(PlayerCreateRoomS2CPacket::class.java)
        registerPacket(PlayerJoinRoomC2SPacket::read)
        registerPacket(PlayerLeaveRoomC2SPacket::read)
        registerPacket(PlayerLeaveRoomS2CPacket::class.java)
        registerPacket(RoomInfoS2CPacket::class.java)

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
                transaction {
                    processPacket(clientAddr,packet)
                }
            }
            else -> {
                logger.error { "$clientAddr 客户端只能传入c2s包 ID$packetId 不是c2s包" }
                return
            }
        }

    }
    fun createPacket(packetId: Int, data: FriendlyByteBuf): Packet? {
        return packetIdReaders[packetId]?.invoke(data) ?: let {
            logger.error { "找不到ID$packetId 的包" }
            return null
        }
    }
    //服务端处理包
    fun processPacket(clientAddr: InetSocketAddress, packet: Packet) {
        when(packet){
            is BeforeLoginProcessable ->{
                packet.process(clientAddr)
            }
            is AfterLoginProcessable ->{
                val player = Player.getByAddr(clientAddr) ?: let {
                    logger.error { "$clientAddr 想要处理包 ${packet.javaClass.simpleName} 但是此人未登录" }
                    return
                }
                packet.process(player)
            }
            is InRoomProcessable ->{
                val player = Player.getByAddr(clientAddr) ?: let {
                    logger.error { "$clientAddr 想要处理包 ${packet.javaClass.simpleName} 但是此人未登录" }
                    return
                }
                val room = player.nowPlayingRoom ?: let {
                    logger.error { "$player 想要处理包 ${packet.javaClass.simpleName} 但是此人未加入任何房间" }
                    return
                }
                packet.process(player,room)
            }
        }
    }
     
    fun getPacketId(packetClass: Class<out BufferWritable>): Int? {
        return packetWriterClassIds[packetClass]
    }




}