package com.veronnnetworks.veronnwallet.data.grpc.message.request

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.cbor.CBORFactory
import com.google.protobuf.ByteString
import com.veronnnetworks.veronnwallet.auth.HmacHelper
import com.veronnnetworks.veronnwallet.data.grpc.message.MsgHeader
import com.veronnnetworks.veronnwallet.data.grpc.message.TypeMac
import com.veronnnetworks.veronnwallet.data.grpc.message.TypeSerialization
import com.veronnworks.veronnwallet.Request

abstract class MsgPacker {
    @JsonIgnore
    var header: MsgHeader = MsgHeader()

    abstract val sharedSecretKey: ByteArray?
    abstract fun setHeader()
    abstract fun jsonToByteArray(): ByteArray

    fun ByteArray.serialize(): ByteArray = when (header.serializationType) {
        TypeSerialization.CBOR -> with(ObjectMapper(CBORFactory())) {
            writeValueAsBytes(this@serialize)
        }
        TypeSerialization.NONE -> with(ObjectMapper()) {
            writeValueAsBytes(this@serialize)
        }
        else -> with(ObjectMapper()) {
            writeValueAsBytes(this@serialize)
        }
    }

    fun ByteArray.generateHmac(key: ByteArray): ByteArray? =
        when (header.macType) {
            TypeMac.HMAC -> HmacHelper.getHmacSignature(key, this)
            else -> null
        }

    fun toByteArray(key: ByteArray?): ByteArray {
        val result = header.toByteArray() + jsonToByteArray().serialize()
        key?.let { return result + result.generateHmac(key)!! } ?: return result
    }

    fun toGrpcMsg(): Request {
        return Request
            .newBuilder()
            .setMessage(ByteString.copyFrom(toByteArray(sharedSecretKey)))
            .build()
    }
}
