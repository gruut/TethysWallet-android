package com.veronnnetworks.veronnwallet.data.grpc.message.request

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.cbor.CBORFactory
import com.google.protobuf.ByteString
import com.veronnnetworks.veronnwallet.data.grpc.message.MsgHeader
import com.veronnnetworks.veronnwallet.data.grpc.message.TypeSerialization
import com.veronnworks.veronnwallet.Request
import java.io.ByteArrayOutputStream

abstract class MsgPacker {
    @JsonIgnore
    var header: MsgHeader = MsgHeader()

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

    fun toByteArray(): ByteArray {
        val outputStream = ByteArrayOutputStream()
        outputStream.write(header.toByteArray())
        outputStream.write(jsonToByteArray().serialize())
        // TODO: Add MAC

        return outputStream.toByteArray()
    }

    fun toGrpcMsg(): Request {
        return Request.newBuilder().setMessage(ByteString.copyFrom(toByteArray())).build()
    }
}