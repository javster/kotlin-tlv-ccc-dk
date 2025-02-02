package com.javster.tlv

import java.nio.ByteBuffer
import java.nio.ByteOrder

class TLV(
    val tag: Int,
    var value: ByteArray = byteArrayOf()
) {
    init {
        if (tag > 65535) {
            throw IllegalArgumentException("TAG value can't be greater than 65535")
        }
    }

    fun serialize(): ByteArray {
        val tagMvb = (tag shr 8).toByte()
        val isTwoBytesTag = tagMvb.isTwoByteTag()
        val tag = if (isTwoBytesTag) {
            ByteBuffer.allocate(2).putShort(tag.toShort()).array()
        } else {
            byteArrayOf(tag.toByte())
        }
        val lengthBytes = when (val length = value.size) {
            in 0..0x7F -> {
                byteArrayOf(length.toByte())
            }
            in 0x80..0xFF -> {
                byteArrayOf((0x81).toByte(), length.toByte())
            }
            in 0x100..0xFFFF -> {
                byteArrayOf((0x82).toByte()) + ByteBuffer.allocate(2).putShort(length.toShort()).array()
            }
            else -> {
                throw IllegalArgumentException("Can't be more than 65535")
            }
        }
        return tag + lengthBytes + value
    }
}

fun parseTlv(bytes: ByteArray): List<TLV> {
    val tlvs = mutableListOf<TLV>()
    var currentIndex = 0
    while (currentIndex < bytes.size) {
        val isTwoBytesTag = bytes[currentIndex].isTwoByteTag()
        val tag: Int = if (isTwoBytesTag) {
            ByteBuffer.wrap(byteArrayOf(bytes[currentIndex], bytes[currentIndex + 1])).apply {
                order(ByteOrder.BIG_ENDIAN)
            }.getShort().toInt()
        } else {
            bytes[currentIndex].toInt() and 0xFF
        }

        val tagSize = if (isTwoBytesTag) 2 else 1

        val lengthPartitionStart = currentIndex + tagSize
        val extraLength = 0.coerceAtLeast((bytes[lengthPartitionStart].toInt() and 0xFF) - 0x80)
        val lengthSize = if (extraLength == 0) {
            1
        } else {
            extraLength + 1
        }

        val length = if (lengthSize == 1) {
            bytes[lengthPartitionStart].toInt()
        } else {
            val v = bytes.slice(lengthPartitionStart + 1..<lengthPartitionStart + lengthSize).toByteArray()
            ByteBuffer.wrap(ByteArray(4) { index ->
                val offset = v.size - 4 + index
                if (offset < 0) 0 else v[offset]
            }).getInt()
        }

        if (lengthSize == 0) {
            throw IllegalArgumentException("Size can't be 0")
        }
        if (lengthSize > 3) {
            throw IllegalArgumentException("Can't process lengths more than 65535")
        }
        if (length > bytes.size - tagSize - lengthSize) {
            throw IllegalArgumentException("Length value and real length are different")
        }

        val offset = lengthPartitionStart + lengthSize
        val value = bytes.slice(offset..<offset + length).toByteArray()
        currentIndex += tagSize + lengthSize + length
        tlvs.add(TLV(tag, value))
    }

    return tlvs
}

/**
 * CCC DK specific interpretation
 * e.g. 7F, 5F, 9F
**/
fun Byte.isTwoByteTag(): Boolean {
    val i = this.toInt() and 0xFF
    return i % 16 == 0xF
}