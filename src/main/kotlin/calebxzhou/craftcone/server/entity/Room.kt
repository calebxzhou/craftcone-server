package calebxzhou.craftcone.server.entity

import calebxzhou.craftcone.net.ConeNetSender
import calebxzhou.craftcone.net.FriendlyByteBuf
import calebxzhou.craftcone.net.protocol.BufferWritable
import calebxzhou.craftcone.net.protocol.Packet
import calebxzhou.craftcone.net.protocol.game.WriteBlockC2SPacket
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
data class Room(
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
    val players = hashMapOf<Int, Player>()

    //启动房间
    fun start(){
        runningRooms += Pair(id, this)
        logger.info { "$this 房间已启动" }
    }

    //新增房间
    fun insert(): RoomInfoRow {
        return RoomInfoRow.new {
            name = this@Room.name
            ownerId = this@Room.ownerId
            mcVersion = this@Room.mcVersion
            isFabric = this@Room.isFabric
            isCreative = this@Room.isCreative
            blockStateAmount = this@Room.blockStateAmount
            seed = this@Room.seed
            createTime = this@Room.createTime
        }
    }

    //玩家加入
    fun playerJoin(player: Player){
        players += Pair(player.id,player)
    }
    //玩家离开
    fun playerLeave(player: Player) {
        players.remove(player.id)
    }

    //保存方块状态&id
    /*fun saveBlockState(id: Int, state: String) {
        val statePath = profilePath.resolve("block_state/")
        Files.createDirectories(statePath)
        Files.writeString(statePath.resolve("$id"), state)
    }*/
    //读方块
    fun readBlock(dimId:Int,chunkPos: ChunkPos, doForEachBlockStateId : (BlockPos,Int)->Unit){
        BlockStateTable.select {
            (BlockStateTable.chunkPos eq chunkPos.asInt)
                .and (BlockStateTable.roomId eq id)
                .and(BlockStateTable.dimId eq dimId)
        }.forEach {
            val bsid = it[BlockStateTable.blockStateId]
            val bpos = BlockPos(it[BlockStateTable.blockPos])
            doForEachBlockStateId.invoke(bpos,bsid)
        }

    }

    //写方块
    fun writeBlock(packet: WriteBlockC2SPacket) {
        BlockStateTable.upsert(BlockStateTable.roomId,BlockStateTable.dimId,BlockStateTable.blockPos) {
            it[roomId]= id
            it[dimId] = packet.dimId
            it[blockPos] = packet.bpos.asLong
            it[chunkPos] = packet.cpos.asInt
            it[blockStateId] = packet.stateId
        }
    }


    //广播数据包（除了自己）
    fun broadcastPacket(packet: BufferWritable,sender : Player) {
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
        private val runningRooms = hashMapOf<Int, Room>()
        //房间是否运行中
        fun isRunning(rid: Int): Boolean {
            return runningRooms.containsKey(rid)
        }
        fun getRunning(rid: Int) : Room?{
            return runningRooms[rid]
        }
        //房间是否存在
        fun exists(rid: Int): Boolean {
            return !RoomInfoTable.select { RoomInfoTable.id eq rid }.empty()
        }
        //创建房间
        fun create(
            player: Player,
            name: String,
            mcVersion: String,
            creative: Boolean,
            fabric: Boolean,
            blockStateAmount: Int
        ): Int {
            Room(
                0,
                name,
                player.id,
                mcVersion,
                fabric,
                creative,
                blockStateAmount,
                Random.nextLong(),
                System.currentTimeMillis()
            ).also {
                return it.insert().id.value
            }
        }
        //读取房间信息
        private fun readFromRow(row: RoomInfoRow): Room {
            return Room(
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
        fun read(rid: Int): Room? {
            return readFromRow(RoomInfoRow.findById(rid)?:return null)
        }
        //查房主ID
        fun getOwnerId(rid: Int) : Int{
            val room = RoomInfoRow.findById(rid)?:return -1
            return room.ownerId
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