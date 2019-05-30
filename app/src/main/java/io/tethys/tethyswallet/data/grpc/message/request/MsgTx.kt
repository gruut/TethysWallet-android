package io.tethys.tethyswallet.data.grpc.message.request

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import io.tethys.tethyswallet.data.grpc.message.TypeMsg
import io.tethys.tethyswallet.data.grpc.message.request.contracts.StandardContractInput
import io.tethys.tethyswallet.utils.TethysConfigs

data class MsgTx constructor(
    @JsonProperty("txid") // __BASE58_256__
    val txid: String,
    @JsonProperty("time") // __TIMESTAMP__
    val time: Int,
    @JsonProperty("world") // __ALPHA_8__
    val world: String,
    @JsonProperty("chain") // __ALPHA_8__
    val chain: String,
    @JsonProperty("body")
    val body: TxBody,
    @JsonProperty("user")
    val user: User,
    @JsonProperty("endorser")
    val endorser: Array<Endorser>
) : MsgPacker() {

    data class TxBody(
        @JsonProperty("cid") // __CONTRACT_ID__
        val contractId: String,
        @JsonProperty("receiver") // __BASE58_256__
        val receiver: String,
        @JsonProperty("fee") // __DECIMAL__
        val fee: Int,
        @JsonProperty("input")
        val input: Array<StandardContractInput>
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as TxBody

            if (!input.contentEquals(other.input)) return false

            return true
        }

        override fun hashCode(): Int = input.contentHashCode()
    }

    data class User(
        @JsonProperty("id") // __BASE58_256__
        val id: String,
        @JsonProperty("pk") // __PEM__ / __PK__
        val pk: String?,
        @JsonProperty("sig") // __BASE64_SIG__
        val sig: String
    )

    data class Endorser(
        @JsonProperty("id") // __BASE58_256__
        val id: String,
        @JsonProperty("pk") // __PEM__ / __PK__
        val pk: String?,
        @JsonProperty("sig") // __BASE64_SIG__
        val sig: String
    )

    @JsonIgnore
    override val sharedSecretKey: ByteArray? = null

    init {
        setHeader()
    }

    override fun setHeader() {
        this.header.msgType = TypeMsg.MSG_TX
        this.header.worldId = world
        this.header.chainId = chain
        this.header.sender = user.id
        this.header.totalLength = TethysConfigs.HEADER_LENGTH + jsonToByteArray().serialize().size
    }

    override fun jsonToByteArray(): ByteArray {
        with(ObjectMapper()) {
            return writeValueAsBytes(this@MsgTx)
        }
    }

    override fun toString(): String {
        return header.toString() + "\n" + String(jsonToByteArray())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MsgTx

        if (!endorser.contentEquals(other.endorser)) return false

        return true
    }

    override fun hashCode(): Int = endorser.contentHashCode()
}