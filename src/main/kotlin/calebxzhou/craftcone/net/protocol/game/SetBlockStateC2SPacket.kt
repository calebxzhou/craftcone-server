package calebxzhou.craftcone.net.protocol.game

/**
 * Created  on 2023-08-02,13:27.
 */
/*
data class SetBlockStateC2SPacket(
    val id:Int,
    val bState:String
):C2SPacket {
    companion object : ReadablePacket<SetBlockStateC2SPacket>{
        override fun read(buf: FriendlyByteBuf): SetBlockStateC2SPacket {
            return SetBlockStateC2SPacket(buf.readVarInt(),buf.readUtf())
        }

    }

    override fun process(clientAddress: InetSocketAddress) {
        //玩家
        val player = ConePlayer.getByAddr(clientAddress)?:return
        player.nowPlayingRoom?.saveBlockState(id,bState)
    }

}*/
