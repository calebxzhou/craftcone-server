package calebxzhou.craftcone.server.model

import java.net.InetSocketAddress
import java.util.*

/**
 * Created  on 2023-07-18,21:02.
 */
data class ConePlayer(
    val pid: UUID,
    val pName: String,
    val pwd: String,
    val addr: InetSocketAddress
)
