package calebxzhou.craftcone.server.model

import calebxzhou.craftcone.server.Consts.DATA_DIR
import java.net.InetSocketAddress
import java.nio.file.Path
import java.util.*
import kotlin.io.path.Path

/**
 * Created  on 2023-07-18,21:02.
 */
data class ConePlayer(
    val pid: UUID,
    val pName: String,
    val pwd: String,
    val addr: InetSocketAddress
){
    companion object{
        const val PWD_FILE = "password.day"
        fun getProfilePath(pid: UUID): Path {
            return Path("$DATA_DIR/players/$pid")
        }
    }
}
