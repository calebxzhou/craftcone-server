package calebxzhou.craftcone.net.protocol

import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.account.LoginRequestPacket
import calebxzhou.craftcone.net.protocol.account.LoginResponsePacket
import calebxzhou.craftcone.net.protocol.game.ConeChatPacket
import calebxzhou.craftcone.net.protocol.game.ConePlayerJoinPacket
import calebxzhou.craftcone.net.protocol.game.ConePlayerQuitPacket
import calebxzhou.craftcone.net.protocol.game.ConeSetBlockPacket

/**
 * Created  on 2023-07-14,8:55.
 */
object ConePacketSet {
    object InGame{
        fun getPacketId(packetClass: Class<out ConeInGamePacket>): Int? {
            return classToId[packetClass]
        }
        fun createPacket(packetId : Int, data : FriendlyByteBuf): ConeInGamePacket {
            return idToReader[packetId].invoke(data)
        }
        private val classToId = linkedMapOf<Class<out ConeInGamePacket>, Int>()
        private val idToReader = arrayListOf<(FriendlyByteBuf) -> ConeInGamePacket>()
        init {
            addPackets(
                Pair(ConeSetBlockPacket::class.java,ConeSetBlockPacket::read),
                Pair(ConeChatPacket::class.java,ConeChatPacket::read),
                Pair(ConePlayerJoinPacket::class.java,ConePlayerJoinPacket::read),
                Pair(ConePlayerQuitPacket::class.java,ConePlayerQuitPacket::read),
            )
        }
        private fun addPackets(vararg packetClassAndReader: Pair<Class<out ConeInGamePacket>,(FriendlyByteBuf) -> ConeInGamePacket>){
            packetClassAndReader.forEach {
                val packetClass = it.first
                val packetReader = it.second
                addPacket(packetClass,packetReader)
            }
        }
        private fun addPacket(packetClass: Class<out ConeInGamePacket>, packetReader : (FriendlyByteBuf) -> ConeInGamePacket){
            val size = idToReader.size
            classToId[packetClass] = size
            idToReader += packetReader
        }
    }

    object OutGame{
        private val classToId = linkedMapOf<Class<out ConeOutGamePacket>, Int>()
        private val idToReader = arrayListOf<(FriendlyByteBuf) -> ConeOutGamePacket>()
        fun getPacketId(packetClass: Class<out ConeOutGamePacket>): Int? {
            return classToId[packetClass]
        }
        fun createPacket(packetId : Int, data : FriendlyByteBuf): ConeOutGamePacket {
            return idToReader[packetId].invoke(data)
        }
        init {
            addPackets(
                Pair(LoginRequestPacket::class.java,LoginRequestPacket::read),
                Pair(LoginResponsePacket::class.java,LoginResponsePacket::read),
            )
        }
        private fun addPackets(vararg packetClassAndReader: Pair<Class<out ConeOutGamePacket>,(FriendlyByteBuf) -> ConeOutGamePacket>){
            packetClassAndReader.forEach {
                val packetClass = it.first
                val packetReader = it.second
                addPacket(packetClass,packetReader)
            }
        }
        private fun addPacket(packetClass: Class<out ConeOutGamePacket>, packetReader : (FriendlyByteBuf) -> ConeOutGamePacket){
            val size = idToReader.size
            classToId[packetClass] = size
            idToReader += packetReader
        }
        
    }
    

}