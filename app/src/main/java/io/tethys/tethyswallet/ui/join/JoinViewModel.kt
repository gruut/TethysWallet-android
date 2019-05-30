package io.tethys.tethyswallet.ui.join

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.tethys.tethyswallet.auth.KeyStoreHelper
import io.tethys.tethyswallet.data.GARepository
import io.tethys.tethyswallet.data.api.response.SignUpResponse
import io.tethys.tethyswallet.data.local.PreferenceHelper
import io.tethys.tethyswallet.model.UserInfo
import io.tethys.tethyswallet.ui.Result
import io.tethys.tethyswallet.ui.common.mapper.toResult
import io.tethys.tethyswallet.utils.ext.map
import io.tethys.tethyswallet.utils.rx.SchedulerProvider
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