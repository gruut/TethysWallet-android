package io.tethys.tethyswallet.data.api.request

import com.squareup.moshi.Json

data class SignUpRequest(
    @Json(name = "phone")
    val mobile: String,
    @Json(name = "csr")
    val csr: String
)