package io.tethys.tethyswallet.data

import androidx.annotation.CheckResult
import io.tethys.tethyswallet.data.api.response.SignUpResponse
import io.reactivex.Flowable

interface GARepository {
    @CheckResult
    fun signUp(
        mobile: String,
        csr: String
    ): Flowable<SignUpResponse>
}