package com.veronnnetworks.veronnwallet.data.grpc.message.request

import com.google.protobuf.ByteString
import com.veronnnetworks.veronnwallet.data.grpc.message.MsgHeader
import com.veronnworks.veronnwallet.Request
import java.io.ByteArrayOutputStream

abstract class MsgPacker {
    var header: MsgHeader = MsgHeader()
    lateinit var receiverId: String

    abstract fun setHeader()
    abstract fun toJson(): ByteArray

    fun ByteArray.serialize(): ByteArray {
        // TODO: Serialize data
        return this
    }

    fun toByteArray(): ByteArray {
        val outputStream = ByteArrayOutputStream()
        outputStream.write(header.toByteArray())
        outputStream.write(toJson().serialize())
        // TODO: Add MAC

        return outputStream.toByteArray()
    }

    fun toGrpcMsg(): Request {
        return Request.newBuilder().setMessage(ByteString.copyFrom(toByteArray())).build()
    }
}