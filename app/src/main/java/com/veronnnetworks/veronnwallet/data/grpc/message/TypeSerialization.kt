package com.veronnnetworks.veronnwallet.data.grpc.message

enum class TypeSerialization(val type: Byte) {
    LZ4(0x04.toByte()),
    MessagePack(0x05.toByte()),
    CBOR(0x06.toByte()),
    NONE(0xFF.toByte());

    companion object {
        fun getByValue(value: Byte) = values().firstOrNull { it.type == value }
    }
}