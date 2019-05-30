package io.tethys.tethyswallet.utils

object TethysConfigs {
    const val IDENTIFIER: Byte = 'P'.toByte()
    const val MSG_VERSION: Byte = 0x01
    const val TEST_WORLD_ID = "TETHYS"
    const val TEST_CHAIN_ID = "TSTCHAIN"

    const val IDENTIFIER_LENGTH: Int = 1
    const val VERSION_LENGTH: Int = 1
    const val MSG_TYPE_LENGTH: Int = 1
    const val MAC_TYPE_LENGTH: Int = 1
    const val SERIALIZATION_TYPE_LENGTH: Int = 1
    const val DUMMY_LENGTH: Int = 1
    const val MSG_LENGTH_SIZE: Int = 4
    const val CHAIN_ID_TYPE_SIZE: Int = 8
    const val WORLD_ID_TYPE_SIZE: Int = 8
    const val SENDER_ID_TYPE_SIZE: Int = 32

    const val HEADER_LENGTH: Int = IDENTIFIER_LENGTH + VERSION_LENGTH + MSG_TYPE_LENGTH +
            MAC_TYPE_LENGTH + SERIALIZATION_TYPE_LENGTH + DUMMY_LENGTH + MSG_LENGTH_SIZE +
            WORLD_ID_TYPE_SIZE + CHAIN_ID_TYPE_SIZE + SENDER_ID_TYPE_SIZE

    const val GRPC_TIMEOUT: Long = 5
}