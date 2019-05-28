package com.veronnnetworks.veronnwallet.data.grpc

import com.veronnnetworks.veronnwallet.data.grpc.message.request.MsgPacker
import com.veronnnetworks.veronnwallet.data.grpc.message.response.MsgUnpacker
import com.veronnnetworks.veronnwallet.utils.VeronnConfigs.GRPC_TIMEOUT
import com.veronnnetworks.veronnwallet.utils.rx.SchedulerProvider
import com.veronnworks.veronnwallet.GruutUserServiceGrpc
import com.veronnworks.veronnwallet.Message
import com.veronnworks.veronnwallet.Reply
import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver
import io.reactivex.Observable
import io.reactivex.Single
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
            GruutUserServiceGrpc.newBlockingStub(channel)
                .withDeadlineAfter(GRPC_TIMEOUT, TimeUnit.SECONDS)
                .userService(msg.toGrpcMsg())
        }.subscribeOn(schedulerProvider.io())
    }

    fun keyExService(msg: MsgPacker): Single<ByteArray> {
        Timber.d(msg.toString())

        return Single.fromCallable {
            val reply = GruutUserServiceGrpc.newBlockingStub(channel)
                .withDeadlineAfter(GRPC_TIMEOUT, TimeUnit.SECONDS)
                .keyExService(msg.toGrpcMsg())

            Timber.d("[%s] %s", reply.status.toString(), reply.message.toStringUtf8())
            when (reply.status) {
                Reply.Status.SUCCESS -> reply.message.toByteArray()
                else -> throw Exception("Error Message From Merger: ${reply.status.name}")
            }
        }.subscribeOn(schedulerProvider.io())
    }

    fun openChannel(): Observable<Message> = asObservable<Message> {
        GruutUserServiceGrpc.newStub(channel).openChannel(it)
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