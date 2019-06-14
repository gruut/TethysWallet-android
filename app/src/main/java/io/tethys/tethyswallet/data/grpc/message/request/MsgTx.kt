package io.tethys.tethyswallet.data.grpc.message.request

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import io.tethys.tethyswallet.data.grpc.message.TypeMsg
import io.tethys.tethyswallet.data.tethys.contracts.StandardContractInput
import io.tethys.tethyswallet.utils.TethysConfigs

data class MsgTx constructor(
    @get:JsonProperty("txid") // __BASE58_256__
    val txid: String,
    @get:JsonProperty("time") // __TIMESTAMP__
    val time: String,
    @get:JsonProperty("world") // __ALPHA_8__
    val world: String,
    @get:JsonProperty("chain") // __ALPHA_8__
    val chain: String,
    @get:JsonProperty("body")
    val body: TxBody,
    @get:JsonProperty("user")
    val user: User,
    @get:JsonProperty("endorser")
    val endorser: Array<Endorser>
) : MsgPacker() {

    data class TxBody(
        @get:JsonProperty("cid") // __CONTRACT_ID__
        val contractId: String,
        @get:JsonProperty("receiver") // __BASE58_256__
        val receiver: String?,
        @get:JsonProperty("fee") // __DECIMAL__
        val fee: Int,
        @get:JsonProperty("input")
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
        @get:JsonProperty("id") // __BASE58_256__
        val id: String,
        @get:JsonProperty("pk") // __PEM__ / __PK__
        val pk: String?,
        @get:JsonProperty("sig") // __BASE64_SIG__
        val sig: String
    )

    data class Endorser(
        @get:JsonProperty("id") // __BASE58_256__
        val id: String,
        @get:JsonProperty("pk") // __PEM__ / __PK__
        val pk: String?,
        @get:JsonProperty("sig") // __BASE64_SIG__
        val sig: String
    )

    @JsonIgnore
    override var sharedSecretKey: ByteArray? = null

    init {
        setHeader()
    }

    override fun setHeader() {
        this.header.msgType = TypeMsg.MSG_TX
        this.header.worldId = world
        this.header.chainId = chain
        this.header.sender = user.id
        this.header.totalLength = TethysConfigs.HEADER_LENGTH + serialize().size
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