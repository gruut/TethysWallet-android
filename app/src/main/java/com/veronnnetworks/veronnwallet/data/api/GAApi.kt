package com.veronnnetworks.veronnwallet.data.api

import androidx.annotation.CheckResult
import com.veronnnetworks.veronnwallet.data.api.request.SignUpRequest
import com.veronnnetworks.veronnwallet.data.api.response.SignUpResponse
import io.reactivex.Flowable
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import javax.inject.Singleton

@Singleton
interface GAApi {

    @Headers("Content-type: application/json")
    @POST("users")
    @CheckResult
    fun signUp(@Body signUpRequest: SignUpRequest): Flowable<SignUpResponse>
}