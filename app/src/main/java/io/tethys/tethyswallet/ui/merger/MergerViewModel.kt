package io.tethys.tethyswallet.ui.merger

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.zipWith
import io.tethys.tethyswallet.Reply
import io.tethys.tethyswallet.auth.KeyStoreHelper
import io.tethys.tethyswallet.data.grpc.GrpcService
import io.tethys.tethyswallet.data.grpc.message.request.MsgSetupMerger
import io.tethys.tethyswallet.ui.Result
import io.tethys.tethyswallet.ui.common.mapper.toResult
import io.tethys.tethyswallet.utils.ext.map
import io.tethys.tethyswallet.utils.rx.SchedulerProvider
import timber.log.Timber
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

    fun sendUserInfo(ip: String?, port: Int?, password: String?) {
        val grpcService = GrpcService(ip!!, port!!, schedulerProvider)

        keyStoreHelper.getCertificatePem()
            .zipWith(keyStoreHelper.getEncryptedSecretKeyPem(password!!)) { cert: String, sk: String? ->
                sk?.let { MsgSetupMerger(it, cert) }
            }
            .flatMap { msg: MsgSetupMerger ->
                grpcService.userService(msg)
            }
            .doFinally { grpcService.terminateChannel() }
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