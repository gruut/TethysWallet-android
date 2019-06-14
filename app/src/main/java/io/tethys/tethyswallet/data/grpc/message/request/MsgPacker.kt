package io.tethys.tethyswallet.data.grpc.message.request

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.cbor.CBORFactory
import com.google.protobuf.ByteString
import io.tethys.tethyswallet.Request
import io.tethys.tethyswallet.auth.HmacHelper
import io.tethys.tethyswallet.data.grpc.message.MsgHeader
import io.tethys.tethyswallet.data.grpc.message.TypeMac
import io.tethys.tethyswallet.data.grpc.message.TypeSerialization

abstract class MsgPacker {
    @JsonIgnore
    var header: MsgHeader = MsgHeader()

    abstract var sharedSecretKey: ByteArray?
    abstract fun setHeader()

    fun serialize(): ByteArray = when (header.serializationType) {
        TypeSerialization.CBOR -> with(ObjectMapper(CBORFactory())) {
            writeValueAsBytes(this@MsgPacker)
        }
        TypeSerialization.NONE -> with(ObjectMapper()) {
            writeValueAsBytes(this@MsgPacker)
        }
        else -> with(ObjectMapper()) {
            writeValueAsBytes(this@MsgPacker)
        }
    }

    fun ByteArray.generateHmac(key: ByteArray): ByteArray? =
        when (header.macType) {
            TypeMac.HMAC -> HmacHelper.getHmacSignature(key, this)
            else -> null
        }

    fun toByteArray(key: ByteArray?): ByteArray {
        val result = header.toByteArray() + serialize()
        key?.let { return result + result.generateHmac(key)!! } ?: return result
    }

    fun toGrpcMsg(): Request {
        return Request
            .newBuilder()
            .setMessage(ByteString.copyFrom(toByteArray(sharedSecretKey)))
            .build()
    }

    override fun toString(): String {
        return header.toString() + "\n" + String(ObjectMapper().writeValueAsBytes(this@MsgPacker))
    }
}
