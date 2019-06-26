package io.tethys.tethyswallet.data.grpc

import com.google.protobuf.ByteString
import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver
import io.reactivex.Observable
import io.reactivex.Single
import io.tethys.tethyswallet.Identity
import io.tethys.tethyswallet.Message
import io.tethys.tethyswallet.Reply
import io.tethys.tethyswallet.TethysUserServiceGrpc
import io.tethys.tethyswallet.data.grpc.message.request.MsgPacker
import io.tethys.tethyswallet.data.grpc.message.response.MsgUnpacker
import io.tethys.tethyswallet.utils.TethysConfigs.GRPC_TIMEOUT
import io.tethys.tethyswallet.utils.ext.decodeBase58
import io.tethys.tethyswallet.utils.rx.SchedulerProvider
import timber.log.Timber
import java.util.concurrent.TimeUnit

class GrpcService constructor(
    ip: String,
    port: Int,
    private val schedulerProvider: SchedulerProvider
) {
    private val channel = ManagedChannelBuilder.forAddress(ip, port).usePlaintext().build()

    fun userService(msg: MsgPacker): Single<ByteArray> =
        Single.fromCallable {
            Timber.d(msg.toString())
            TethysUserServiceGrpc.newBlockingStub(channel)
                .withDeadlineAfter(GRPC_TIMEOUT, TimeUnit.SECONDS)
                .userService(msg.toGrpcMsg())
                .run {
                    Timber.d(
                        "[%s] %s",
                        this.status.toString(),
                        msg.header.msgType.toString()
                    )
                    when (this.status) {
                        Reply.Status.SUCCESS -> this.message.toByteArray()
                        else -> throw Exception("Error Message From Merger: [${this.status.name}] ${this.errInfo}")
                    }
                }
        }.subscribeOn(schedulerProvider.io())

    fun signerService(msg: MsgPacker): Single<Reply.Status> =
        Single.fromCallable {
            Timber.d(msg.toString())
            TethysUserServiceGrpc.newBlockingStub(channel)
                .withDeadlineAfter(GRPC_TIMEOUT, TimeUnit.SECONDS)
                .signerService(msg.toGrpcMsg())
                .run {
                    Timber.d(
                        "[%s] %s",
                        this.status.toString(),
                        this.errInfo
                    )

                    this.status
                }
        }.onErrorResumeNext(Single.just(Reply.Status.INVALID))
            .subscribeOn(schedulerProvider.io())

    fun keyExService(msg: MsgPacker): Single<ByteArray> =
        Single.fromCallable {
            Timber.d(msg.toString())
            TethysUserServiceGrpc.newBlockingStub(channel)
                .withDeadlineAfter(GRPC_TIMEOUT, TimeUnit.SECONDS)
                .keyExService(msg.toGrpcMsg())
                .run {
                    Timber.d(
                        "[%s] %s",
                        this.status.toString(),
                        MsgUnpacker(this.message.toByteArray())
                    )
                    when (this.status) {
                        Reply.Status.SUCCESS -> this.message.toByteArray()
                        else -> throw Exception("Error Message From Merger: [${this.status.name}] ${this.errInfo}")
                    }
                }
        }.subscribeOn(schedulerProvider.io())


    fun pushService(base58Id: String): Observable<Message> = asObservable<Message> {
        Identity.newBuilder().setSender(ByteString.copyFrom(base58Id.decodeBase58())).build()
            .apply {
                TethysUserServiceGrpc.newStub(channel).pushService(this, it)
            }
    }.subscribeOn(schedulerProvider.io())

    fun terminateChannel() {
        channel.shutdownNow()
    }

    inline fun <T> asObservable(
        crossinline body: (StreamObserver<T>) -> Unit
    ): Observable<T> {
        return Observable.create { subscription ->
            val observer = object : StreamObserver<T> {
                override fun onNext(value: T) {
                    subscription.onNext(value)
                }

                override fun onError(error: Throwable) {
                    subscription.onError(error)
                }

                override fun onCompleted() {
                    subscription.onComplete()
                }
            }
            body(observer)
        }
    }
}