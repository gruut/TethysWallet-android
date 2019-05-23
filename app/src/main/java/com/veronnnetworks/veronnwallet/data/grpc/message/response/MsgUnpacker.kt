package com.veronnnetworks.veronnwallet.data.grpc.message.response

import com.veronnnetworks.veronnwallet.data.grpc.message.MsgHeader
import com.veronnnetworks.veronnwallet.data.grpc.message.TypeMac
import com.veronnnetworks.veronnwallet.data.grpc.message.TypeSerialization
import com.veronnnetworks.veronnwallet.utils.VeronnConfigs.HEADER_LENGTH

abstract class MsgUnpacker(
    bytes: ByteArray
) {
    abstract fun byteArrayToJson(): MsgUnpacker
    abstract fun checkSender(): Boolean

    val header: MsgHeader
    val body: ByteArray
    val mac: ByteArray

    init {
        var offset = 0
        header = MsgHeader(bytes.copyOfRange(offset, HEADER_LENGTH))
        offset += HEADER_LENGTH

        body = bytes.copyOfRange(offset, header.totalLength).deserialize()
        offset += (header.totalLength - HEADER_LENGTH)

        mac = bytes.copyOfRange(offset, bytes.size)
    }

    fun ByteArray.deserialize(): ByteArray = when (header.serializationType) {
        // TODO: Deserialize data
        TypeSerialization.NONE -> this
        else -> this
    }

    fun checkMac(): Boolean = when (header.macType) {
        // TODO: check MAC
        TypeMac.NONE -> true
        else -> true
    }

}