# 📦 Kotlin DK TLV Library 

Kotlin DK TLV Library is a convenient way to support TLV in the way it provided in Connectivity Car Concorcium Digital Key documentation.

✨ Key features

✅ 2-byte tags correct serialization/deserialization (5F, 7F, 9F like)

✅ Simple and straightforward API

## Quick start

```
//Simple TLV
val myTlv = TLV(0x80, byteArrayOf(0x00, 0x01))
val tlv = parseTlv(tlvBytes)[0] 
//tlv now contains byte array 0x80020001

//Nested TLV
val nestedTlv = TLV(0x69, 
     TLV(0x70, byteArrayOf(0x25, 0x34)).serialize() // 0x70022534
).serialize()
//tlv now contains serialized value 0x700470022534



