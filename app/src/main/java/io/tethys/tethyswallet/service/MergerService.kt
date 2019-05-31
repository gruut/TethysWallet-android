package io.tethys.tethyswallet.service

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import dagger.android.AndroidInjection
import dagger.android.DaggerService
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.zipWith
import io.tethys.tethyswallet.Message
import io.tethys.tethyswallet.Reply
import io.tethys.tethyswallet.auth.KeyStoreHelper
import io.tethys.tethyswallet.data.grpc.GrpcService
import io.tethys.tethyswallet.data.grpc.message.TypeMode
import io.tethys.tethyswallet.data.grpc.message.request.*
import io.tethys.tethyswallet.data.grpc.message.response.*
import io.tethys.tethyswallet.data.local.PreferenceHelper
import io.tethys.tethyswallet.ui.BaseApp
import io.tethys.tethyswallet.utils.TethysConfigs
import io.tethys.tethyswallet.utils.ext.*
import io.tethys.tethyswallet.utils.rx.SchedulerProvider
import timber.log.Timber
import javax.inject.Inject

class MergerService : DaggerService() {

    private val prefHelper: PreferenceHelper = BaseApp.prefHelper

    @Inject
    lateinit var keyStoreHelper: KeyStoreHelper
    @Inject
    lateinit var schedulerProvider: SchedulerProvider

    private lateinit var grpcService: GrpcService
    private lateinit var sharedSecretKey: ByteArray
    private var dhKeyExchanged: Boolean = false
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
        grpcService.terminateChannel()
        super.onDestroy()
    }

    private fun connectWithMerger() {
        val msgJoin = MsgJoin(
            getTimestamp(),
            TethysConfigs.TEST_WORLD_ID,
            TethysConfigs.TEST_CHAIN_ID,
            prefHelper.commonName!!,
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
            }.doOnError {
                Timber.e(it)
            }.doOnSuccess {
                dhKeyExchanged = (MsgUnpacker(it).body as MsgAccept).result
                Timber.d(MsgUnpacker(it).toString())

                // Signer Setup
                if (prefHelper.isSigner) readyForSign()
            }
            .subscribe()
            .addTo(compositeDisposable)
    }

    private fun readyForSign() =
        grpcService.pushService(prefHelper.commonName!!)
            .flatMapSingle { msg: Message ->
                val msgReqSuccess =
                    MsgUnpacker(msg.toByteArray(), sharedSecretKey).body as MsgReqSsig

                if (!msgReqSuccess.validateId()) {
                    Single.fromCallable {
                        MsgError(
                            prefHelper.commonName!!,
                            getTimestamp(),
                            Reply.Status.INVALID,
                            "Invalid block id in MSG_REQ_SSIG"
                        )
                    }
                } else {
                    msgReqSuccess.generateResponse()
                }
            }
            .flatMapSingle { msg: MsgPacker ->
                grpcService.signerService(msg)
            }
            .doOnComplete { Timber.d("Stream was closed") }
            .doOnError { throwable -> Timber.e(throwable) }
            .subscribe()
            .addTo(compositeDisposable)

    private fun MsgChallenge.generateResponse(): Single<MsgResponse1> {
        val nonce = getNonce(256)
        val time = getTimestamp()
        val point = prefHelper.ecPublicKey.hexToPointPair()
        return keyStoreHelper.getCertificatePem()
            .zipWith(
                keyStoreHelper.signWithECKey(
                    this.mergerNonce.fromBase64() +
                            nonce.fromBase64() +
                            point.first.toByteArray() +
                            point.second.toByteArray() +
                            time.longBytes()
                )
            ) { cert, signature ->
                MsgResponse1(
                    time,
                    nonce,
                    MsgResponse1.DHJson(point.first, point.second),
                    MsgResponse1.UserJson(
                        prefHelper.commonName!!,
                        cert,
                        signature
                    )
                )
            }
    }

    private fun Triple<MsgChallenge, MsgResponse1, MsgResponse2>.generateResponse(): Single<MsgSuccess> =
        keyStoreHelper.verifyWithCert( // verify merger's signature
            first.mergerNonce.fromBase64() +
                    second.un.fromBase64() +
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
                prefHelper.commonName!!,
                TypeMode.SIGNER.mode,
                t.first
            ).apply {
                this.sharedSecretKey = this@MergerService.sharedSecretKey
            }
        }

    private fun MsgReqSsig.generateResponse(): Single<MsgSsig> =
        keyStoreHelper.signWithECKey(
            (this.block.id.decodeBase58() +
                    this.block.txroot.fromBase64() +
                    this.block.usroot.fromBase64() +
                    this.block.csroot.fromBase64()).toSha256()
        ).map { signature ->
            MsgSsig(
                MsgSsig.Block(this.block.id),
                MsgSsig.Signer(
                    prefHelper.commonName!!,
                    signature
                )
            ).apply {
                this.sharedSecretKey = this@MergerService.sharedSecretKey
            }
        }
}
