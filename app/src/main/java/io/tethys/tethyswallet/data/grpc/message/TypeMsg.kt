package io.tethys.tethyswallet.data.grpc.message

enum class TypeMsg(val type: Byte) {
    MSG_NULL(0x00.toByte()),

    MSG_JOIN(0x54.toByte()),
    MSG_CHALLENGE(0x55.toByte()),
    MSG_RESPONSE_1(0x56.toByte()),
    MSG_RESPONSE_2(0x57.toByte()),
    MSG_SUCCESS(0x58.toByte()),
    MSG_ACCEPT(0x59.toByte()),

    MSG_TX(0xB1.toByte()),
    MSG_REQ_SSIG(0xB2.toByte()),
    MSG_SSIG(0xB3.toByte()),

    MSG_REQ_TX_CHECK(0xC0.toByte()),
    MSG_RES_TX_CHECK(0xC1.toByte()),
    MSG_QUERY(0xC3.toByte()),
    MSG_RESULT(0xC4.toByte()),

    MSG_SETUP_MERGER(0xE1.toByte()),
    MSG_ERROR(0xFF.toByte());

    companion object {
        fun getByValue(value: Byte) = values().firstOrNull { it.type == value }
    }
}