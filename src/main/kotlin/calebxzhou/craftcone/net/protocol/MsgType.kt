package calebxzhou.craftcone.net.protocol

/**
 * Created  on 2023-08-15,7:42.
 */
enum class MsgType(val id:Int) {
    Dialog(0),
    Toast(1),
    Chat(2),
    ;
    companion object {
        //id取类型
        private val map = values().associateBy { it.id }
        operator fun get(value: Int) = map[value]?: Dialog
    }

}