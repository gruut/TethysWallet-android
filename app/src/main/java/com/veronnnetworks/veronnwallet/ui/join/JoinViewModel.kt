package com.veronnnetworks.veronnwallet.ui.join

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.veronnnetworks.veronnwallet.auth.KeyStoreHelper
import com.veronnnetworks.veronnwallet.data.GARepository
import com.veronnnetworks.veronnwallet.data.api.response.SignUpResponse
import com.veronnnetworks.veronnwallet.data.local.PreferenceHelper
import com.veronnnetworks.veronnwallet.model.UserInfo
import com.veronnnetworks.veronnwallet.ui.Result
import com.veronnnetworks.veronnwallet.ui.common.mapper.toResult
import com.veronnnetworks.veronnwallet.utils.ext.map
import com.veronnnetworks.veronnwallet.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber
import javax.inject.Inject

class JoinViewModel @Inject constructor(
    private val keyStoreHelper: KeyStoreHelper,
    private val gaRepository: GARepository,
    private val preferenceHelper: PreferenceHelper,
    private val schedulerProvider: SchedulerProvider
) : ViewModel(), LifecycleObserver {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private val mutableGenerateState: MutableLiveData<Result<Unit>> = MutableLiveData()
    val generateResult: LiveData<Result<Unit>> = mutableGenerateState

    private val mutableStoreState: MutableLiveData<Result<Unit>> = MutableLiveData()
    val storeResult: LiveData<Result<Unit>> = mutableStoreState

    val isLoading: LiveData<Boolean> by lazy {
        generateResult.map { it.inProgress }
        storeResult.map { it.inProgress }
    }

    fun isKeyExist(): Boolean {
        return !preferenceHelper.commonName.isNullOrEmpty()
    }

    fun generateECKeyPair() {
        keyStoreHelper.generateECKeyPair()
            .toResult<Unit>(schedulerProvider)
            .subscribeBy(
                onError = { Timber.e(it) },
                onNext = { mutableGenerateState.value = it }
            )
            .addTo(compositeDisposable)
    }

    fun getCertificate(userInfo: UserInfo) {
        keyStoreHelper.generateCSRPem()
            .flatMapPublisher { csr: String ->
                gaRepository.signUp(userInfo.mobile!!, csr)
            }
            .flatMapSingle { response: SignUpResponse ->
                keyStoreHelper.putCertificatePem(response.pem)
            }
            .toResult<Unit>(schedulerProvider)
            .subscribeBy(
                onError = { Timber.e(it) },
                onNext = { mutableStoreState.value = it }
            )
            .addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}