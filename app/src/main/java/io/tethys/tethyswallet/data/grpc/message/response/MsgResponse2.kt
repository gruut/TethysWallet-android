package io.tethys.tethyswallet.data.grpc.message.response

import com.fasterxml.jackson.annotation.JsonProperty

data class MsgResponse2(
    @JsonProperty("time") // __TIMESTAMP__
    override val time: Int,
    @JsonProperty("dh")
    val dh: DHJson,
    @JsonProperty("merger")
    val mergerInfo: MergerJson
) : MsgBody {
    override val merger: String
        get() = mergerInfo.id

    data class DHJson constructor(
        @JsonProperty("x") // __HEX_256__
        val x: String,
        @JsonProperty("y") // __HEX_256__
        val y: String
    )

    data class MergerJson constructor(
        @JsonProperty("id") // __BASE58_256__
        val id: String,
        @JsonProperty("cert") // __CERT_PEM__
        val cert: String,
        @JsonProperty("sig") // __BASE64_SIG__
        val sig: String
    )
}