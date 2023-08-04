package calebxzhou.craftcone.net.protocol

/**
 * Created  on 2023-08-03,21:30.
 */
interface ResponsePacket {
    val ok:Boolean
    val data:String
}