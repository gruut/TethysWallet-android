package io.tethys.tethyswallet.service

import androidx.core.text.isDigitsOnly
import io.reactivex.rxkotlin.subscribeBy
import io.tethys.tethyswallet.data.grpc.GrpcService
import io.tethys.tethyswallet.data.grpc.message.request.MsgTx
import io.tethys.tethyswallet.ui.common.mapper.toResult
import io.tethys.tethyswallet.utils.rx.AppSchedulerProvider
import org.spongycastle.util.IPAddress
import timber.log.Timber

class MessageSenderService {
    private val schedulerProvider = AppSchedulerProvider()

    fun send(ipAddress: String, port: String, transaction: MsgTx) {
        if (IPAddress.isValid(ipAddress) && port.isDigitsOnly()) {
            val grpcService = GrpcService(ipAddress, port.toInt(), schedulerProvider)

            grpcService.userService(transaction)
                .doFinally { grpcService.terminateChannel() }
                .toResult(schedulerProvider)
                .subscribeBy(
                    onError = { Timber.e(it) },
                    onNext = { Timber.d(it.toString()) }
                )
        }
    }
}
