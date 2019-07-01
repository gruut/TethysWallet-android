package io.tethys.tethyswallet.service

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.text.isDigitsOnly
import dagger.android.AndroidInjection
import dagger.android.DaggerService
import io.reactivex.rxkotlin.subscribeBy
import io.tethys.tethyswallet.data.grpc.GrpcService
import io.tethys.tethyswallet.data.grpc.message.request.MsgPacker
import io.tethys.tethyswallet.data.grpc.message.request.MsgTx
import io.tethys.tethyswallet.ui.common.mapper.toResult
import io.tethys.tethyswallet.utils.rx.SchedulerProvider
import org.spongycastle.util.IPAddress
import timber.log.Timber
import javax.inject.Inject

class MessageSenderService: DaggerService() {
    @Inject
    lateinit var schedulerProvider: SchedulerProvider

    private val binder = MessageSenderBinder()

    inner class MessageSenderBinder : Binder() {
        fun getService(): MessageSenderService = this@MessageSenderService
    }

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    fun send(ipAddress: String, port: String, msg: MsgPacker) {
        if (IPAddress.isValid(ipAddress) && port.isDigitsOnly()) {
            val grpcService = GrpcService(ipAddress, port.toInt(), schedulerProvider)

            grpcService.userService(msg)
                .doFinally { grpcService.terminateChannel() }
                .toResult(schedulerProvider)
                .subscribeBy(
                    onError = { Timber.e(it) },
                    onNext = {
                        Timber.d(it.toString())
                    }
                )
        }
    }
}
