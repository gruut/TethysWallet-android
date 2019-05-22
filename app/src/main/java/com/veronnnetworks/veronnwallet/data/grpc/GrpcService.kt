package com.veronnnetworks.veronnwallet.data.grpc

import com.veronnnetworks.veronnwallet.data.grpc.message.request.MsgPacker
import com.veronnnetworks.veronnwallet.utils.VeronnConfigs.GRPC_TIMEOUT
import com.veronnnetworks.veronnwallet.utils.rx.SchedulerProvider
import com.veronnworks.veronnwallet.GruutUserServiceGrpc
import com.veronnworks.veronnwallet.Reply
import io.grpc.ManagedChannelBuilder
import io.reactivex.Single
import java.util.concurrent.TimeUnit

class GrpcService constructor(
    ip: String,
    port: Int,
    private val schedulerProvider: SchedulerProvider
) {
    private val channel = ManagedChannelBuilder.forAddress(ip, port).usePlaintext().build()
    private val stub = GruutUserServiceGrpc.newBlockingStub(channel)

    fun userService(msg: MsgPacker): Single<Reply> {
        return Single.fromCallable {
            stub.withDeadlineAfter(GRPC_TIMEOUT, TimeUnit.SECONDS)
                .userService(msg.toGrpcMsg())
        }.subscribeOn(schedulerProvider.io())
    }

    fun terminateChannel() {
        channel.shutdownNow()
    }
}