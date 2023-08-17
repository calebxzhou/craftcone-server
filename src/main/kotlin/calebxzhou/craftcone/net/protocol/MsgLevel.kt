package calebxzhou.craftcone.net.protocol

/**
 * Created  on 2023-08-13,20:57.
 */
enum class MsgLevel(val id:Int) {
    Normal(0),
    Info(1),
    Ok(2),
    Warn(3),
    Err(4),
    ;

    companion object {
        //id取类型
        private val map = MsgLevel.values().associateBy { it.id }
        operator fun get(value: Int) = map[value]?: Info
    }

}