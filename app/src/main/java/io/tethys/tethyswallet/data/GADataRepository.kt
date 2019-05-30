package io.tethys.tethyswallet.data

import androidx.annotation.CheckResult
import io.tethys.tethyswallet.data.api.GAApi
import io.tethys.tethyswallet.data.api.request.SignUpRequest
import io.tethys.tethyswallet.data.api.response.SignUpResponse
import io.tethys.tethyswallet.utils.rx.SchedulerProvider
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