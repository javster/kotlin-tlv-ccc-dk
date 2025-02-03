# 📦 Kotlin DK TLV Library 

Kotlin DK TLV Library is a convenient way to support TLV in the way it provided in Connectivity Car Concorcium Digital Key documentation.

✨ Key features

✅ 2-byte tags correct serialization/deserialization (5F, 7F, 9F like)

✅ Simple and straightforward API

## Quick start

```
//Creates TLV with 0x80 tag and 0x0001 data
val myTlv = TLV(0x80, byteArrayOf(0x00, 0x01))
//TLV serialization to byte array (0x80020001)
val tlvBytes = myTlv.serialize()
//TLV deserialization to List<TLV>
val tlv = deserialize(tlvBytes)

//Nested TLV
val nestedTlv = TLV(0x69, 
    TLV(0x70, byteArrayOf(0x25, 0x34)).serialize() 
).serialize()




