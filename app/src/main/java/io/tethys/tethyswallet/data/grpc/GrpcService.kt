package io.tethys.tethyswallet.data.grpc

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
import io.tethys.tethyswallet.utils.rx.SchedulerProvider
import timber.log.Timber
import java.util.concurrent.TimeUnit

class GrpcService constructor(
    ip: String,
    port: Int,
    private val schedulerProvider: SchedulerProvider
) {
    private val channel = ManagedChannelBuilder.forAddress(ip, port).usePlaintext().build()

    fun userService(msg: MsgPacker): Single<Reply> {
        return Single.fromCallable {
            TethysUserServiceGrpc.newBlockingStub(channel)
                .withDeadlineAfter(GRPC_TIMEOUT, TimeUnit.SECONDS)
                .userService(msg.toGrpcMsg())
        }.subscribeOn(schedulerProvider.io())
    }

    fun keyExService(msg: MsgPacker): Single<ByteArray> {
        Timber.d(msg.toString())

        return Single.fromCallable {
            val reply = TethysUserServiceGrpc.newBlockingStub(channel)
                .withDeadlineAfter(GRPC_TIMEOUT, TimeUnit.SECONDS)
                .keyExService(msg.toGrpcMsg())

            Timber.d("[%s] %s", reply.status.toString(), MsgUnpacker(reply.message.toByteArray()))
            when (reply.status) {
                Reply.Status.SUCCESS -> reply.message.toByteArray()
                else -> throw Exception("Error Message From Merger: ${reply.status.name}")
            }
        }.subscribeOn(schedulerProvider.io())
    }

    fun reqSsigService(identity: Identity): Observable<Message> = asObservable<Message> {
        TethysUserServiceGrpc.newStub(channel).reqSsigService(identity, it)
    }.subscribeOn(schedulerProvider.io())

    fun terminateChannel() {
        channel.shutdownNow()
    }
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