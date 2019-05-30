package io.tethys.tethyswallet.data.grpc.message.response

import com.fasterxml.jackson.annotation.JsonProperty

data class MsgChallenge constructor(
    @JsonProperty("time") // __TIMESTAMP__
    val time: Int,
    @JsonProperty("user") // __BASE58_256__
    val user: String,
    @JsonProperty("merger") // __BASE58_256__
    override val merger: String,
    @JsonProperty("mn") // __BASE64_256__
    val mergerNonce: String
) : MsgBody