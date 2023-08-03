package calebxzhou.craftcone.misc

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.net.InetSocketAddress

/**
 * Created  on 2023-08-03,20:38.
 */
object InetSocketAddressSerializer : KSerializer<InetSocketAddress> {
    override val descriptor  = PrimitiveSerialDescriptor("InetSocketAddress", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): InetSocketAddress {
        val split = decoder.decodeString().split(":")
        return InetSocketAddress(split[0],split[1].toInt())
    }

    override fun serialize(encoder: Encoder, value: InetSocketAddress) {
        encoder.encodeString("${value.hostString}:${value.port}")
    }
}