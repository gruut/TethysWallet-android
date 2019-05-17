package com.veronnnetworks.veronnwallet.ui.merger

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import com.veronnnetworks.veronnwallet.auth.KeyStoreHelper
import com.veronnnetworks.veronnwallet.data.grpc.GrpcService
import com.veronnnetworks.veronnwallet.data.grpc.message.request.MsgSetupMerger
import com.veronnnetworks.veronnwallet.model.MergerInfo
import com.veronnnetworks.veronnwallet.ui.common.mapper.toResult
import com.veronnnetworks.veronnwallet.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.zipWith
import javax.inject.Inject

class MergerViewModel @Inject constructor(
    private val keyStoreHelper: KeyStoreHelper,
    private val schedulerProvider: SchedulerProvider
) : ViewModel(), LifecycleObserver {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun sendUserInfo(mergerInfo: MergerInfo) {
        if (!mergerInfo.ip.isNullOrEmpty() && !mergerInfo.port.isNullOrEmpty()) {
            val grpcService =
                GrpcService(mergerInfo.ip!!, mergerInfo.port!!.toInt(), schedulerProvider)

            keyStoreHelper.getCertificatePem()
                .zipWith(keyStoreHelper.getEncryptedSecretKeyPem("test")) { cert: String, sk: String? ->
                    sk?.let { MsgSetupMerger(it, cert) }
                }
                .flatMap { msg: MsgSetupMerger -> grpcService.userService(msg) }
                .toResult(schedulerProvider)
                .subscribeBy {
                    // TODO: user result
                }
                .addTo(compositeDisposable)
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}