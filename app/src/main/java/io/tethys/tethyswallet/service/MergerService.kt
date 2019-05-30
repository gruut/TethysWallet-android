package io.tethys.tethyswallet.service

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import io.tethys.tethyswallet.auth.KeyStoreHelper
import io.tethys.tethyswallet.data.grpc.GrpcService
import io.tethys.tethyswallet.data.grpc.message.request.MsgJoin
import io.tethys.tethyswallet.data.grpc.message.request.MsgResponse1
import io.tethys.tethyswallet.data.grpc.message.request.MsgSuccess
import io.tethys.tethyswallet.data.grpc.message.response.MsgChallenge
import io.tethys.tethyswallet.data.grpc.message.response.MsgResponse2
import io.tethys.tethyswallet.data.grpc.message.response.MsgUnpacker
import io.tethys.tethyswallet.data.local.PreferenceHelper
import io.tethys.tethyswallet.utils.TethysConfigs
import io.tethys.tethyswallet.utils.ext.*
import io.tethys.tethyswallet.utils.rx.SchedulerProvider
import dagger.android.AndroidInjection
import dagger.android.DaggerService
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.zipWith
import timber.log.Timber
import javax.inject.Inject

class MergerService : DaggerService() {

    @Inject
    lateinit var keyStoreHelper: KeyStoreHelper
    @Inject
    lateinit var preferenceHelper: PreferenceHelper
    @Inject
    lateinit var schedulerProvider: SchedulerProvider

    private lateinit var grpcService: GrpcService
    private lateinit var sharedSecretKey: ByteArray
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private val binder = MergerBinder()

    inner class MergerBinder : Binder() {
        fun getService(): MergerService = this@MergerService
    }

    companion object {
        const val INTENT_MERGER_IP = "INTENT_MERGER_IP"
        const val INTENT_MERGER_PORT = "INTENT_MERGER_PORT"

        fun from(binder: IBinder): MergerService {
            return (binder as MergerBinder).getService()
        }
    }

    override fun onBind(intent: Intent): IBinder {
        grpcService = GrpcService(
            intent.getStringExtra(INTENT_MERGER_IP),
            intent.getIntExtra(INTENT_MERGER_PORT, 0),
            schedulerProvider
        )
        connectWithMerger()
        return binder
    }

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        grpcService.terminateChannel()
    }

    private fun connectWithMerger() {
        val msgJoin = MsgJoin(
            getTimestamp(),
            TethysConfigs.TEST_WORLD_ID,
            TethysConfigs.TEST_CHAIN_ID,
            preferenceHelper.commonName!!,
            "" // TODO: Set merger's id based on data from TRACKER
        )

        grpcService.keyExService(msgJoin)
            .flatMap { msgChallengeBytes ->
                val msgChallenge = MsgUnpacker(msgChallengeBytes).body as MsgChallenge
                msgChallenge.generateResponse()
                    .map { msgResponse1: MsgResponse1 ->
                        Pair(msgChallenge, msgResponse1)
                    }
            }.flatMap { t: Pair<MsgChallenge, MsgResponse1> ->
                grpcService.keyExService(t.second)
                    .map { msgResponse2Bytes ->
                        Triple(
                            t.first,
                            t.second,
                            MsgUnpacker(msgResponse2Bytes).body as MsgResponse2
                        )
                    }
            }.flatMap { t: Triple<MsgChallenge, MsgResponse1, MsgResponse2> ->
                t.generateResponse()
            }.flatMap { msgSuccess ->
                grpcService.keyExService(msgSuccess)
            }.subscribeBy(
                onError = { Timber.e(it) },
                onSuccess = {
                    Timber.d(MsgUnpacker(it).toString())
                }
            ).addTo(compositeDisposable)
    }

    private fun MsgChallenge.generateResponse(): Single<MsgResponse1> {
        val nonce = getNonce(256)
        val time = getTimestamp()
        val point = preferenceHelper.ecPublicKey.hexToPointPair()
        return keyStoreHelper.getCertificatePem()
            .zipWith(
                keyStoreHelper.signWithECKey(
                    this.mergerNonce.fromBase64() +
                            nonce.fromBase64() +
                            point.first +
                            point.second +
                            time.longBytes()
                )
            ) { cert, signature ->
                MsgResponse1(
                    time,
                    nonce,
                    MsgResponse1.DHJson(point.first.toString(), point.second.toString()),
                    MsgResponse1.UserJson(
                        preferenceHelper.commonName!!,
                        cert,
                        signature
                    )
                )
            }
    }

    private fun Triple<MsgChallenge, MsgResponse1, MsgResponse2>.generateResponse(): Single<MsgSuccess> {
        return keyStoreHelper.verifyWithCert( // verify merger's signature
            first.mergerNonce.fromBase64() +
                    second.userNonce.fromBase64() +
                    third.dh.x.toByteArray(Charsets.UTF_8) +
                    third.dh.y.toByteArray(Charsets.UTF_8) +
                    third.time.longBytes(),
            third.mergerInfo.sig,
            third.mergerInfo.cert
        ).flatMap { result ->
            // point 에서 public key 추출
            val mergerPubKey = Pair(
                third.dh.x.toByteArray(Charsets.UTF_8),
                third.dh.y.toByteArray(Charsets.UTF_8)
            ).pointPairToHex().hexToPublicKey()

            // shared secret key 생성
            keyStoreHelper.getSharedSecretKey(mergerPubKey).map {
                Pair(result, it)
            }
        }.map { t: Pair<Boolean, ByteArray> ->
            sharedSecretKey = t.second

            MsgSuccess(
                getTimestamp(),
                preferenceHelper.commonName!!,
                t.first,
                sharedSecretKey
            )
        }
    }
}
