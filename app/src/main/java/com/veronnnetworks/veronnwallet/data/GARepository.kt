package com.veronnnetworks.veronnwallet.data

import androidx.annotation.CheckResult
import com.veronnnetworks.veronnwallet.data.api.response.SignUpResponse
import io.reactivex.Flowable

interface GARepository {
    @CheckResult
    fun signUp(
        mobile: String,
        csr: String
    ): Flowable<SignUpResponse>
}