package com.veronnnetworks.veronnwallet.data.grpc.message.response

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.cbor.CBORFactory
import com.veronnnetworks.veronnwallet.data.grpc.message.MsgHeader
import com.veronnnetworks.veronnwallet.data.grpc.message.TypeMac
import com.veronnnetworks.veronnwallet.data.grpc.message.TypeMsg
import com.veronnnetworks.veronnwallet.data.grpc.message.TypeSerialization
import com.veronnnetworks.veronnwallet.utils.VeronnConfigs.HEADER_LENGTH

class MsgUnpacker(
    bytes: ByteArray
) {
    val header: MsgHeader
    val body: MsgBody?
    val mac: ByteArray

    init {
        var offset = 0
        header = MsgHeader(bytes.copyOfRange(offset, HEADER_LENGTH))
        offset += HEADER_LENGTH

        body = bytes.copyOfRange(offset, header.totalLength).deserialize()
        offset += (header.totalLength - HEADER_LENGTH)

        mac = bytes.copyOfRange(offset, bytes.size)
    }

    private fun getMapper(): ObjectMapper = when (header.serializationType) {
        TypeSerialization.CBOR -> ObjectMapper(CBORFactory())
        TypeSerialization.NONE -> ObjectMapper()
        else -> ObjectMapper()
    }

    private fun ByteArray.deserialize(): MsgBody? = when (header.msgType) {
        TypeMsg.MSG_CHALLENGE -> getMapper().readValue(this, MsgChallenge::class.java)
        else -> null
    }

    fun checkMac(): Boolean = when (header.macType) {
        // TODO: check MAC
        TypeMac.NONE -> true
        else -> true
    }

    fun checkSender(): Boolean = header.sender.equals(body?.merger)

    override fun toString(): String {
        return header.toString() + "\n" + body.toString()
    }
}