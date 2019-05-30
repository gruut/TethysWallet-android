package io.tethys.tethyswallet.data.grpc.message.response

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.cbor.CBORFactory
import io.tethys.tethyswallet.auth.HmacHelper
import io.tethys.tethyswallet.data.grpc.message.MsgHeader
import io.tethys.tethyswallet.data.grpc.message.TypeMac
import io.tethys.tethyswallet.data.grpc.message.TypeMsg
import io.tethys.tethyswallet.data.grpc.message.TypeSerialization
import io.tethys.tethyswallet.utils.TethysConfigs.HEADER_LENGTH

class MsgUnpacker(
    bytes: ByteArray,
    val sharedSecretKey: ByteArray? = null
) {
    val header: MsgHeader
    val serializedBody: ByteArray
    val body: MsgBody?
    val mac: ByteArray

    init {
        var offset = 0
        header = MsgHeader(bytes.copyOfRange(offset, HEADER_LENGTH))
        offset += HEADER_LENGTH

        serializedBody = bytes.copyOfRange(offset, header.totalLength)
        body = serializedBody.deserialize()
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
        TypeMsg.MSG_RESPONSE_2 -> getMapper().readValue(this, MsgResponse2::class.java)
        TypeMsg.MSG_ACCEPT -> getMapper().readValue(this, MsgAccept::class.java)
        TypeMsg.MSG_REQ_SSIG -> getMapper().readValue(this, MsgReqSsig::class.java)
        TypeMsg.MSG_RES_TX_CHECK -> getMapper().readValue(this, MsgResTxCheck::class.java)
        else -> null
    }

    fun checkMac(): Boolean = when (header.macType) {
        TypeMac.HMAC -> {
            sharedSecretKey?.let {
                HmacHelper.verifyHmacSignature(
                    sharedSecretKey,
                    serializedBody,
                    mac
                )
            } ?: false
        }
        TypeMac.NONE -> true
        else -> true
    }

    fun checkSender(): Boolean = header.sender.equals(body?.merger)

    override fun toString(): String {
        return header.toString() + "\n" + body.toString()
    }
}