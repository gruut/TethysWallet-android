package com.veronnnetworks.veronnwallet.data

import androidx.annotation.CheckResult
import com.veronnnetworks.veronnwallet.data.api.GAApi
import com.veronnnetworks.veronnwallet.data.api.request.SignUpRequest
import com.veronnnetworks.veronnwallet.data.api.response.SignUpResponse
import com.veronnnetworks.veronnwallet.utils.rx.SchedulerProvider
import io.reactivex.Flowable
import javax.inject.Inject

class GADataRepository @Inject constructor(
    private val api: GAApi,
    private val schedulerProvider: SchedulerProvider
) : GARepository {

    @CheckResult
    override fun signUp(mobile: String, csr: String): Flowable<SignUpResponse> {
        return api.signUp(SignUpRequest(mobile, csr))
    }
}