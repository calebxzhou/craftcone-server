package calebxzhou.craftcone.net

import calebxzhou.craftcone.net.protocol.AfterLoginProcessable
import calebxzhou.craftcone.net.protocol.BeforeLoginProcessable
import calebxzhou.craftcone.net.protocol.InRoomProcessable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.server.entity.ConeOnlinePlayer
import calebxzhou.craftcone.server.entity.ConeRoom
import calebxzhou.craftcone.server.logger
import io.netty.channel.ChannelHandlerContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created  on 2023-08-26,11:02.
 */
object ConePacketProcessor {
    val procScope = CoroutineScope(Dispatchers.Default)
    //服务端处理包
    fun processPacket(ctx: ChannelHandlerContext, packet: Packet) = procScope.launch{
        when(packet){
            is BeforeLoginProcessable ->{
                packet.process(ctx)
            }

            is AfterLoginProcessable -> ConeOnlinePlayer.getByNetCtx(ctx)?.let {
                packet.process(it)
            }

            is InRoomProcessable -> ConeOnlinePlayer.getByNetCtx(ctx)?.let { player ->
                ConeRoom.getPlayerPlayingRoom(player.data.id)?.let { room ->
                    packet.process(player,room)
                }?:run {
                    logger.info{ "Player ${player.data} send an InRoomPacket but not in room!" }
                }
            }

        }
    }

}