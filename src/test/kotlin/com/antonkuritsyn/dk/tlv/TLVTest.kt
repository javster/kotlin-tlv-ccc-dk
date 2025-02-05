package com.antonkuritsyn.dk.tlv

import com.antonkuritsyn.dk.tlv.TLV.Companion.deserialize
import java.nio.ByteBuffer
import kotlin.test.Test

@OptIn(ExperimentalStdlibApi::class)
class TlvTest {

    @Test
    fun test1() {
        //Creates TLV with 0x80 tag and 0x0001 data
        val myTlv = TLV(0x80, byteArrayOf(0x00, 0x01))
        //TLV serialization to byte array (0x80020001)
        val tlvBytes = myTlv.serialize()
        //TLV deserialization to List<TLV>
        val tlv = deserialize(tlvBytes)
    }

    @Test
    fun bigPayloadTest() {
        val tlvBytes = TLV(ONE_BYTE_TAG, ByteBuffer.allocate(BIG_PAYLOAD_LENGTH).array()).serialize()
        val tlv = deserialize(tlvBytes)[0]
        assert(tlv.value.size == BIG_PAYLOAD_LENGTH)
    }

    @Test
    fun oneByteTagTest() {
        val tlv = TLV(ONE_BYTE_TAG, TWO_BYTE_VALUE.hexToByteArray())
        val serializationResult = tlv.serialize()
        assert(serializationResult.toHexString().contentEquals(ONE_BYTE_TAG_TLV))

        val deserialized = deserialize(serializationResult)
        assert(deserialized.size == 1)

        val firstTag = deserialized[0]
        assert(firstTag.tag == ONE_BYTE_TAG)
        assert(firstTag.value.contentEquals(TWO_BYTE_VALUE.hexToByteArray()))
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun twoByteTagTest() {
        val functionId = TLV(ONE_BYTE_TAG, TWO_BYTE_VALUE.hexToByteArray()).serialize()
        val functionStatus = TLV(TWO_BYTE_TAG, functionId).serialize()

        assert(functionStatus.toHexString().uppercase().contentEquals(TWO_BYTE_TAG_TLV))

        val tlvs = deserialize(functionStatus)
        assert(tlvs.size == 1)

        val rootTag = tlvs[0]
        assert(rootTag.tag == TWO_BYTE_TAG)

        val nestedTlvs = deserialize(rootTag.value)
        assert(nestedTlvs.size == 1)

        val centralLocking = nestedTlvs[0]

        assert(centralLocking.tag == ONE_BYTE_TAG)
        assert(centralLocking.value.contentEquals(TWO_BYTE_VALUE.hexToByteArray()))
    }

    private fun String.hexToByteArray() = chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray()

    companion object {
        private const val ONE_BYTE_TAG = 0x80
        private const val TWO_BYTE_TAG = 0x7F74

        private const val TWO_BYTE_VALUE = "0001"

        private const val ONE_BYTE_TAG_TLV = "80020001"
        private const val TWO_BYTE_TAG_TLV = "7F740480020001"

        private const val BIG_PAYLOAD_LENGTH = 1024
    }
}