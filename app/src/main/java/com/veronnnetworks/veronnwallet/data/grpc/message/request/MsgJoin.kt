package com.veronnnetworks.veronnwallet.data.grpc.message.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.veronnnetworks.veronnwallet.data.grpc.message.TypeMsg
import com.veronnnetworks.veronnwallet.utils.VeronnConfigs

data class MsgJoin constructor(
    @JsonProperty("time") // __TIMESTAMP__
    val time: Int,
    @JsonProperty("world") // __ALPHA_8__
    val world: String,
    @JsonProperty("chain") // __ALPHA_8__
    val chain: String,
    @JsonProperty("user") // __BASE58_256__
    val user: String,
    @JsonProperty("merger") // __BASE58_256__
    val merger: String
) : MsgPacker() {
    init {
        setHeader()
    }

    override fun setHeader() {
        this.header.msgType = TypeMsg.MSG_JOIN
        this.header.worldId = world
        this.header.chainId = chain
        this.header.sender = user
        this.header.totalLength = VeronnConfigs.HEADER_LENGTH + jsonToByteArray().serialize().size
    }

    override fun jsonToByteArray(): ByteArray {
        with(ObjectMapper()) {
            return writeValueAsBytes(this@MsgJoin)
        }
    }

    override fun toString(): String {
        return header.toString() + "\n" + String(jsonToByteArray())
    }
}