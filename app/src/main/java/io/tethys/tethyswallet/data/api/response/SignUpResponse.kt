package io.tethys.tethyswallet.data.api.response

import com.squareup.moshi.Json

data class SignUpResponse(
    @Json(name = "code")
    val code: Int,
    @Json(name = "message")
    val message: String,
    @Json(name = "nid")
    val nid: String,
    @Json(name = "certPem")
    val pem: String
)