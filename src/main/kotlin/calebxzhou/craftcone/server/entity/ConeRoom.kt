package calebxzhou.craftcone.server.entity

import calebxzhou.craftcone.net.ConeNetSender
import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.coneErrD
import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.net.protocol.game.BlockDataC2CPacket
import calebxzhou.craftcone.net.protocol.general.OkDataS2CPacket
import calebxzhou.craftcone.server.logger
import calebxzhou.craftcone.server.table.BlockStateTable
import calebxzhou.craftcone.server.table.RoomInfoRow
import calebxzhou.craftcone.server.table.RoomInfoTable
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.upsert
import kotlin.random.Random

/**
 * Created  on 2023-08-03,13:20.
 */
@Serializable
data class ConeRoom(
    //房间ID
    val id: Int,
    //房间名
    val name: String,
    //房主id
    val ownerId: Int,
    //mc版本
    val mcVersion:String,
    //mod加载器？Fabric：Forge
    val isFabric: Boolean,
    //创造
    val isCreative: Boolean,
    //方块状态数量
    val blockStateAmount: Int,
    //地图种子
    val seed: Long,
    //创建时间
    val createTime: Long,
    ): Packet,BufferWritable {

    override fun write(buf: FriendlyByteBuf) {
        buf.writeVarInt(id)
        buf.writeUtf(name)
        buf.writeVarInt(ownerId)
        buf.writeUtf(mcVersion)
        buf.writeBoolean(isFabric)
        buf.writeBoolean(isCreative)
        buf.writeVarInt(blockStateAmount)
        buf.writeLong(seed)
        buf.writeLong(createTime)
    }

    @Transient
    //正在游玩 当前房间的 玩家list
    val players = hashMapOf<Int, ConePlayer>()

    //启动房间
    fun start(){
        runningRooms += Pair(id, this)
        logger.info { "$this 房间已启动" }
    }

    //新增房间
    fun insert(): RoomInfoRow {
        return RoomInfoRow.new {
            name = this@ConeRoom.name
            ownerId = this@ConeRoom.ownerId
            mcVersion = this@ConeRoom.mcVersion
            isFabric = this@ConeRoom.isFabric
            isCreative = this@ConeRoom.isCreative
            blockStateAmount = this@ConeRoom.blockStateAmount
            seed = this@ConeRoom.seed
            createTime = this@ConeRoom.createTime
        }
    }

    //玩家加入
    fun playerJoin(player: ConePlayer){
        players += Pair(player.id,player)
    }
    //玩家离开
    fun playerLeave(player: ConePlayer) {
        players.remove(player.id)
    }

    //保存方块状态&id
    /*fun saveBlockState(id: Int, state: String) {
        val statePath = profilePath.resolve("block_state/")
        Files.createDirectories(statePath)
        Files.writeString(statePath.resolve("$id"), state)
    }*/
    //读方块
    fun readBlock(dimId:Int, chunkPos: ConeChunkPos, doForEachBlock : (BlockPos, Int, String?)->Unit){
        BlockStateTable.select {
            (BlockStateTable.chunkPos eq chunkPos.asInt)
                .and (BlockStateTable.roomId eq id)
                .and(BlockStateTable.dimId eq dimId)
        }.forEach {
            val bsid = it[BlockStateTable.blockStateId]
            val bpos = BlockPos(it[BlockStateTable.blockPos])
            val tag = it[BlockStateTable.tag]
            doForEachBlock.invoke(bpos,bsid,tag)
        }

    }

    //写方块
    fun writeBlock(packet: BlockDataC2CPacket) {
        BlockStateTable.upsert(BlockStateTable.roomId,BlockStateTable.dimId,BlockStateTable.blockPos) {
            it[roomId]= id
            it[dimId] = packet.dimId
            it[blockPos] = packet.bpos.asLong
            it[chunkPos] = packet.bpos.chunkPos.asInt
            it[blockStateId] = packet.stateId
            it[tag]=packet.tag
        }
    }


    //广播数据包（除了自己）
    fun broadcastPacket(packet: BufferWritable,sender : ConePlayer) {
        players.forEach {
            if(sender.addr != it.value.addr){
                ConeNetSender.sendPacket(packet, it.value.addr)
            }
        }
    }


    /*fun readBlockStateId(dimId: Int, bpos: BlockPos): Int {
        return try {
            Files.readString(profilePath.resolve(getStateIdPath(dimId, bpos))).toInt()
        } catch (e: NoSuchFileException) {
            0
        }
    }*/
    override fun toString(): String {
        return "$name($id)"
    }

    companion object {

        //全部运行中的房间
        private val runningRooms = hashMapOf<Int, ConeRoom>()
        //房间是否运行中
        fun isRunning(rid: Int): Boolean {
            return runningRooms.containsKey(rid)
        }
        fun getRunning(rid: Int) : ConeRoom?{
            return runningRooms[rid]
        }
        //房间是否存在
        fun exists(rid: Int): Boolean {
            return !RoomInfoTable.select { RoomInfoTable.id eq rid }.empty()
        }
        //创建房间
        fun onCreate(
            player: ConePlayer,
            name: String,
            mcVersion: String,
            creative: Boolean,
            fabric: Boolean,
            blockStateAmount: Int
        ) {

            val ownRoom = player.ownRoom
            if(ownRoom != null){
                coneErrD(player,"建过房间了,ID=${ownRoom.id}")
                return
            }
            val rid = ConeRoom(
                0,
                name,
                player.id,
                mcVersion,
                fabric,
                creative,
                blockStateAmount,
                Random.nextLong(),
                System.currentTimeMillis()
            ).insert().id.value
            player.sendPacket(OkDataS2CPacket{it.writeVarInt(rid)})
        }
        //读取房间信息
        fun ofRow(row: RoomInfoRow): ConeRoom {
            return ConeRoom(
                row.id.value,
                row.name,
                row.ownerId,
                row.mcVersion,
                row.isFabric,
                row.isCreative,
                row.blockStateAmount,
                row.seed,
                row.createTime)
        }


        //读取房间数据
        fun read(rid: Int): ConeRoom? {
            return ofRow(RoomInfoRow.findById(rid)?:return null)
        }
        //查房主ID
        fun getOwnerId(rid: Int) : Int{
            val room = RoomInfoRow.findById(rid)?:return -1
            return room.ownerId
        }
        //收到删除请求
        fun onDelete(player: ConePlayer,rid:Int){
            val ownerId = getOwnerId(rid)
            if(ownerId < 0 || ownerId != player.id){
                coneErrD(player,"你没有房间")
                return
            }
            if(delete(rid)){
                player.sendPacket(OkDataS2CPacket())
            }
        }
        //收到读取请求
        fun onGet(player: ConePlayer, rid:Int){
            read(rid)?.let { player.sendPacket(it) }?:run {
                coneErrD(player,"找不到房间$rid")
            }
        }
        //删除房间
        fun delete(rid :Int) : Boolean{
            if(!exists(rid))
                return false
            RoomInfoTable.deleteWhere { RoomInfoTable.id eq rid }
            BlockStateTable.deleteWhere { BlockStateTable.roomId eq rid }
            return true
        }
    }


}