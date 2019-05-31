package io.tethys.tethyswallet.data.grpc.message.response

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

data class MsgResTxCheck(
    @JsonProperty("time") // __TIMESTAMP__
    override val time: Int,
    @JsonProperty("block") // __BASE64__
    val block: String,
    @JsonProperty("confirm") // boolean
    val confirm: Boolean,
    @JsonProperty("proof") // __BASE64__
    val proof: String,
    @JsonProperty("output") // __BASE64__
    val output: String,
    @JsonProperty("merger")
    val mergerInfo: MergerInfo,
    @JsonIgnore
    override val merger: String = mergerInfo.id
) : MsgBody {
    data class MergerInfo(
        @JsonProperty("id") // __BASE58_256__
        val id: String,
        @JsonProperty("sig") // __BASE64_SIG__
        val sig: String
    )
}