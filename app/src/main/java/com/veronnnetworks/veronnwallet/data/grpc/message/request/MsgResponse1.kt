package com.veronnnetworks.veronnwallet.data.grpc.message.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.veronnnetworks.veronnwallet.data.grpc.message.TypeMsg
import com.veronnnetworks.veronnwallet.utils.VeronnConfigs

data class MsgResponse1 constructor(
    @JsonProperty("time") // __TIMESTAMP__
    val time: Int,
    @JsonProperty("un") // __BASE64_256__
    val userNonce: String,
    @JsonProperty("dh")
    val dh: DHJson,
    @JsonProperty("user")
    val user: UserJson
) : MsgPacker() {
    data class DHJson constructor(
        @JsonProperty("x") // __HEX_256__
        val x: String,
        @JsonProperty("y") // __HEX_256__
        val y: String
    )

    data class UserJson constructor(
        @JsonProperty("id") // __BASE58_256__
        val id: String,
        @JsonProperty("pk") // __CERT_PEM__ / __PK__
        val pk: String,
        @JsonProperty("sig") // __BASE64_SIG__
        val sig: String
    )

    init {
        setHeader()
    }

    override fun setHeader() {
        this.header.msgType = TypeMsg.MSG_RESPONSE_1
        this.header.sender = user.id
        this.header.totalLength = VeronnConfigs.HEADER_LENGTH + jsonToByteArray().serialize().size
    }

    override fun jsonToByteArray(): ByteArray {
        with(ObjectMapper()) {
            return writeValueAsBytes(this@MsgResponse1)
        }
    }

    override fun toString(): String {
        return header.toString() + "\n" + String(jsonToByteArray())
    }
}