package com.veronnnetworks.veronnwallet.data.grpc.message

enum class TypeMac(val type: Byte) {
    HMAC(0xF1.toByte()),
    SHA256(0xF2.toByte()),
    NONE(0xFF.toByte());

    companion object {
        fun getByValue(value: Byte) = values().firstOrNull { it.type == value }
    }
}