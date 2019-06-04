package io.tethys.tethyswallet.data.grpc.message.response

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.cbor.CBORFactory
import io.tethys.tethyswallet.Reply
import io.tethys.tethyswallet.auth.HmacHelper
import io.tethys.tethyswallet.data.grpc.message.MsgHeader
import io.tethys.tethyswallet.data.grpc.message.TypeMac
import io.tethys.tethyswallet.data.grpc.message.TypeMsg
import io.tethys.tethyswallet.data.grpc.message.TypeSerialization
import io.tethys.tethyswallet.utils.TethysConfigs.HEADER_LENGTH
import io.tethys.tethyswallet.utils.TethysConfigs.MSG_EXP_TIME
import io.tethys.tethyswallet.utils.ext.getTimestamp

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
        TypeMsg.MSG_RESULT -> getMapper().readValue(this, MsgResult::class.java)
        else -> null
    }

    private fun checkMac(): Boolean = when (header.macType) {
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

    private fun checkSender(): Boolean = body?.let { header.sender.equals(body.merger) } ?: false

    private fun checkTime(): Boolean =
        body?.let { (body.time + MSG_EXP_TIME) > getTimestamp() } ?: false

    fun checkValidity(): Reply.Status {
        if (!checkTime()) return Reply.Status.ECDH_TIMEOUT
        if (!checkSender()) return Reply.Status.ECDH_ILLEGAL_ACCESS
        if (!checkMac()) return Reply.Status.ECDH_INVALID_SIG
        return Reply.Status.SUCCESS
    }

    override fun toString(): String {
        return header.toString() + "\n" + body.toString()
    }
}