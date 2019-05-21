package com.veronnnetworks.veronnwallet.ui.merger

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.veronnnetworks.veronnwallet.auth.KeyStoreHelper
import com.veronnnetworks.veronnwallet.data.grpc.GrpcService
import com.veronnnetworks.veronnwallet.data.grpc.message.request.MsgSetupMerger
import com.veronnnetworks.veronnwallet.model.MergerInfo
import com.veronnnetworks.veronnwallet.ui.Result
import com.veronnnetworks.veronnwallet.ui.common.mapper.toResult
import com.veronnnetworks.veronnwallet.utils.ext.map
import com.veronnnetworks.veronnwallet.utils.rx.SchedulerProvider
import com.veronnworks.veronnwallet.Reply
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.zipWith
import timber.log.Timber
import java.io.InvalidObjectException
import javax.inject.Inject

class MergerViewModel @Inject constructor(
    private val keyStoreHelper: KeyStoreHelper,
    private val schedulerProvider: SchedulerProvider
) : ViewModel(), LifecycleObserver {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private val mutableReply: MutableLiveData<Result<Reply>> = MutableLiveData()
    val reply: LiveData<Result<Reply>> = mutableReply

    val isLoading: LiveData<Boolean> by lazy {
        reply.map { it.inProgress }
    }

    fun sendUserInfo(mergerInfo: MergerInfo) {
        keyStoreHelper.getCertificatePem()
            .zipWith(keyStoreHelper.getEncryptedSecretKeyPem("test")) { cert: String, sk: String? ->
                sk?.let { MsgSetupMerger(it, cert) }
            }
            .flatMap { msg: MsgSetupMerger ->
                if (!mergerInfo.validate()) throw InvalidObjectException("Invalid inputs")
                GrpcService(
                    mergerInfo.ip!!,
                    mergerInfo.port!!.toInt(),
                    schedulerProvider
                ).userService(msg)
            }
            .toResult(schedulerProvider)
            .subscribeBy(
                onError = { Timber.e(it) },
                onNext = { mutableReply.value = it }
            )
            .addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}