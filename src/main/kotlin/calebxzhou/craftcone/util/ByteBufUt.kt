package calebxzhou.craftcone.util

import io.netty.buffer.ByteBuf
import io.netty.handler.codec.DecoderException
import io.netty.handler.codec.EncoderException
import org.bson.types.ObjectId
import java.nio.charset.StandardCharsets

object ByteBufUt {


fun getVarLongSize(input: Long): Int {
    for (i in 1..9) {
        if (input and (-1L shl i * 7) == 0L) {
            return i
        }
    }
    return 10
}
    @JvmStatic
fun getVarIntSize(input: Int): Int {
    for (i in 1..4) {
        if (input and (-1 shl i * 7) == 0) {
            return i
        }
    }
    return 5
}
private fun getMaxEncodedUtfLength(i: Int): Int {
    return i * 3
}
/*fun ByteBuf.writeObjectId(objectId: ObjectId) : ByteBuf{
    writeUtf(objectId.toHexString())
    return this
}
fun ByteBuf.readObjectId():ObjectId = ObjectId(
    readUtf()
)*/
fun ByteBuf.writeObjectId(objectId: ObjectId) : ByteBuf{
    writeBytes(objectId.toByteArray())
    return this
}
    fun ByteBuf.readObjectId(): ObjectId = ObjectId(
        readBytes(12).nioBuffer()
    )
fun ByteBuf.readUtf(): String  {
    return this.readUtf(32767)
}

fun ByteBuf.readUtf(i: Int): String  {
    val j: Int = getMaxEncodedUtfLength(i)
    val k = readVarInt()
    return if (k > j) {
        throw DecoderException("The received encoded string buffer length is longer than maximum allowed ($k > $j)")
    } else if (k < 0) {
        throw DecoderException("The received encoded string buffer length is less than zero! Weird string!")
    } else {
        val string = this.toString(this.readerIndex(), k, StandardCharsets.UTF_8)
        this.readerIndex(this.readerIndex() + k)
        if (string.length > i) {
            val var10002 = string.length
            throw DecoderException("The received string length is longer than maximum allowed ($var10002 > $i)")
        } else {
            string
        }
    }
}

fun ByteBuf.writeUtf(string: String): ByteBuf {
    return this.writeUtf(string, 32767)
}

fun ByteBuf.writeUtf(string: String, i: Int): ByteBuf {
    return if (string.length > i) {
        val var10002 = string.length
        throw EncoderException("String too big (was $var10002 characters, max $i)")
    } else {
        val bs = string.toByteArray(StandardCharsets.UTF_8)
        val j: Int = getMaxEncodedUtfLength(i)
        if (bs.size > j) {
            throw EncoderException("String too big (was " + bs.size + " bytes encoded, max " + j + ")")
        } else {
            writeVarInt(bs.size)
            this.writeBytes(bs)
            this
        }
    }
}

fun ByteBuf.readVarInt(): Int {
    var i = 0
    var j = 0
    var b: Byte
    do {
        b = readByte()
        i = i or (b.toInt() and 0b1111111 shl j++ * 7)
        if (j > 5) {
            throw RuntimeException("VarInt too big")
        }
    } while (b.toInt() and 0b10000000 == 0b10000000)
    return i
}
fun ByteBuf.writeVarInt(i: Int): ByteBuf {
    var input = i
    while (input and -128 != 0) {
        writeByte(input and 0b01111111 or 0b10000000)
        input = input ushr 7
    }
    writeByte(input)
    return this
}

/**
 * Writes an array of VarInts to the buffer, prefixed by the length of the array (as a VarInt).
 *
 * @see .readVarIntArray
 *
 *
 * @param array the array to write
 */
fun ByteBuf.writeVarIntArray(array: IntArray): ByteBuf {
    writeVarInt(array.size)
    for (i in array) {
        writeVarInt(i)
    }
    return this
}

/**
 * Reads an array of VarInts from this buffer.
 *
 * @see .writeVarIntArray
 */
fun ByteBuf.readVarIntArray(): IntArray {
    return readVarIntArray(readableBytes())
}
fun ByteBuf.readVarIntArray(maxLength: Int): IntArray {
    val i = readVarInt()
    return if (i > maxLength) {
        throw DecoderException("VarIntArray with size $i is bigger than allowed $maxLength")
    } else {
        val `is` = IntArray(i)
        for (j in `is`.indices) {
            `is`[j] = readVarInt()
        }
        `is`
    }
}}