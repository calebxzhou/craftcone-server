package calebxzhou.craftcone.net

import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.net.protocol.account.LoginByNameC2SPacket
import calebxzhou.craftcone.net.protocol.account.LoginByUidC2SPacket
import calebxzhou.craftcone.net.protocol.account.RegisterC2SPacket
import calebxzhou.craftcone.net.protocol.game.*
import calebxzhou.craftcone.net.protocol.general.*
import calebxzhou.craftcone.net.protocol.room.*
import calebxzhou.craftcone.server.entity.ConeRoom
import calebxzhou.craftcone.server.logger
import io.netty.buffer.ByteBuf

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
    private val packetIdReaders = linkedMapOf<Int,(ByteBuf) -> Packet>()
    //s2c
    private val packetWriterClassIds = linkedMapOf<Class<out BufferWritable>,Int>()
    init {
        registerPacket(ConeRoom::class.java)

        registerPacket(CloseScreenS2CPacket::class.java)
        registerPacket(CopyToClipboardS2CPacket::class.java)
        registerPacket(DisconnectS2CPacket::class.java)
        registerPacket(GetServerInfoC2SPacket::read)
        registerPacket(OkDataS2CPacket::class.java)
        registerPacket(ServerInfoS2CPacket::class.java)
        registerPacket(SysMsgS2CPacket::class.java)

        registerPacket(LoginByUidC2SPacket::read)
        registerPacket(LoginByNameC2SPacket::read)
        registerPacket(RegisterC2SPacket::read)

        registerPacket(BlockDataS2CPacket::class.java)
        registerPacket(GetChunkC2SPacket::read)
        registerPacket(MovePlayerWpC2SPacket::read)
        registerPacket(MovePlayerXyzC2SPacket::read)
        registerPacket(PlayerJoinedRoomS2CPacket::class.java)
        registerPacket(PlayerLeftRoomS2CPacket::class.java)
        registerPacket(PlayerMoveWpS2CPacket::class.java)
        registerPacket(PlayerMoveXyzS2CPacket::class.java)
        registerPacket(SendChatMsgC2SPacket::read)
        registerPacket(SetBlockC2SPacket::read)

        registerPacket(CreateRoomC2SPacket::read)
        registerPacket(DelRoomC2SPacket::read)
        registerPacket(GetMyRoomC2SPacket::read)
        registerPacket(GetRoomC2SPacket::read)
        registerPacket(JoinRoomC2SPacket::read)
        registerPacket(LeaveRoomC2SPacket::read)

    }

    private fun registerPacket(reader: (ByteBuf) -> Packet){
        packetIdReaders += Pair (packetTypes.size,reader)
        packetTypes += PacketType.READ
    }
    private fun registerPacket(writerClass: Class<out BufferWritable>) {
        packetWriterClassIds += Pair (writerClass, packetTypes.size)
        packetTypes += PacketType.WRITE
    }
    
    
    //客户端传入包 服务端这边创建+处理
    fun create(packetId: Int,data: ByteBuf) :Packet?= packetTypes.getOrNull(packetId)?.run {
        when(this){
            PacketType.READ ->{
                packetIdReaders[packetId] ?.invoke(data)?.let {
                    return it
                }?:run{
                    return null
                }
            }
            else -> {
                return null
            }
        }
    }?:run {
        logger.error { "找不到ID$packetId 的包" }
        return null
    }
        
    
   /* fun createAndProcess(clientctx: ChannelHandlerContext, packetId: Int,  data: ByteBuf)=
        packetTypes.getOrNull(packetId)?.run {
            when(this){
                PacketType.READ ->{
                    packetIdReaders[packetId] ?.invoke(data)?.run {
                        ConePacketProcessor.processPacket(clientAddr,this)
                    }?:run{
                        logger.error { "找不到ID$packetId 的包" }
                    }
                }
                else -> {
                    logger.error { "$clientAddr 客户端只能传入c2s包 ID$packetId 不是c2s包" }
                }
            }
        }?:run {
            logger.error { "找不到ID$packetId 的包" }
        }
*/
    fun getPacketId(packetClass: Class<out BufferWritable>): Int? = packetWriterClassIds[packetClass]




}