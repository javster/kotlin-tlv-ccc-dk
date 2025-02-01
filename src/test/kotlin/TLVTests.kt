package com.javster.tlv

import java.nio.ByteBuffer
import kotlin.test.Test

private const val FUNCTION_ID_CENTRAL_LOCKING = 0x0001
private const val FUNCTION_TAG_GET_FUNCTION_STATUS = 0x7F74
private const val FUNCTION_ID_TAG = 0x80

private const val ONE_BYTE_TLV = "80020001"
private const val TWO_BYTE_TLV = "7F740480020001"

@OptIn(ExperimentalStdlibApi::class)
class TlvTest {

    @Test
    fun twoByteTagTests() {
        assert(0x7F.toByte().isTwoByteTag())
        assert(!0x80.toByte().isTwoByteTag())
        assert(0xFF.toByte().isTwoByteTag())
        assert(!0xFD.toByte().isTwoByteTag())
    }

    @Test
    fun bigPayloadTest() {
        val tlv1 = TLV(FUNCTION_ID_TAG, ByteBuffer.allocate(1024).array())
        val serialized = tlv1.serialize()
        println(serialized.toHexString())
    }

    @Test
    fun oneByteTagTest() {
        val tlv1 = TLV(FUNCTION_ID_TAG, FUNCTION_ID_CENTRAL_LOCKING.)
        val serializationResult = tlv1.serialize()
        assert(serializationResult.toHexString().contentEquals(ONE_BYTE_TLV))

        val deserialized = parseTlv(serializationResult)
        assert(deserialized.size == 1)

        val firstTag = deserialized[0]
        assert(firstTag.tag == FUNCTION_ID_TAG)
        assert(firstTag.value.contentEquals(FUNCTION_ID_CENTRAL_LOCKING))
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun twoByteTagTest() {
        val functionId = TLV(FUNCTION_ID_TAG, FUNCTION_ID_CENTRAL_LOCKING).serialize()
        val functionStatus = TLV(FUNCTION_TAG_GET_FUNCTION_STATUS, functionId).serialize()
        println("function id: ${functionId.toHexString()}, function status: ${functionStatus.toHexString()}")
        assert(functionStatus.toHexString().uppercase().contentEquals(TWO_BYTE_TLV))

        val tlvs = parseTlv(functionStatus)
        assert(tlvs.size == 1)

        val rootTag = tlvs[0]
        assert(rootTag.tag == FUNCTION_TAG_GET_FUNCTION_STATUS)

        val nestedTlvs = parseTlv(rootTag.value)
        assert(nestedTlvs.size == 1)

        val centralLocking = nestedTlvs[0]

        assert(centralLocking.tag == FUNCTION_ID_TAG)
        assert(centralLocking.value.contentEquals(FUNCTION_ID_CENTRAL_LOCKING))
    }
}