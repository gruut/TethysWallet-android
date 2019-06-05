package io.tethys.tethyswallet.data.grpc

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.tethys.tethyswallet.data.grpc.message.TypeMsg
import io.tethys.tethyswallet.data.grpc.message.TypeSerialization
import io.tethys.tethyswallet.data.grpc.message.request.MsgPacker
import io.tethys.tethyswallet.utils.TethysConfigs

data class TestMsgChallenge constructor(
    @get:JsonProperty("time") // __TIMESTAMP__
    val time: Int,
    @get:JsonProperty("user") // __BASE58_256__
    val user: String,
    @get:JsonProperty("merger") // __BASE58_256__
    val merger: String,
    @get:JsonProperty("mn")// __BASE64_256__
    val mergerNonce: String
) : MsgPacker() {
    @JsonIgnore
    override var sharedSecretKey: ByteArray? = null

    init {
        setHeader()
    }

    override fun setHeader() {
        this.header.msgType = TypeMsg.MSG_CHALLENGE
        this.header.serializationType = TypeSerialization.NONE
        this.header.totalLength = TethysConfigs.HEADER_LENGTH + serialize().size
    }

    override fun toString(): String {
        return header.toString() + "\n" + String(serialize())
    }

}