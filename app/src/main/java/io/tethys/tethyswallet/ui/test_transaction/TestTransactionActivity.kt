package io.tethys.tethyswallet.ui.test_transaction

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import io.tethys.tethyswallet.R
import io.tethys.tethyswallet.service.MergerService
import io.tethys.tethyswallet.service.MessageSenderService
import io.tethys.tethyswallet.service.TestTransactionService
import io.tethys.tethyswallet.ui.common.activity.BaseActivity
import kotlinx.android.synthetic.main.activity_test_transaction.*

class TestTransactionActivity : BaseActivity() {
    private lateinit var messageSenderService: MessageSenderService
    private var bound: Boolean = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MessageSenderService.MessageSenderBinder
            messageSenderService = binder.getService()
            bound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_test_transaction)

        sendTransactionButton.setOnClickListener {
            val ipAddress = ipTextView.text.toString()
            if (ipAddress.isEmpty()) {
                ipTextView.setError("A IP address is required!")
            }

            val port = portTextView.text.toString()
            if (port.isEmpty()) {
                ipTextView.setError("A Port number is required!")
            }

            TestTransactionService(messageSenderService).apply {
                this.send(ipAddress, port)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, MessageSenderService::class.java).also { intent ->
            intent.putExtra(MergerService.INTENT_MERGER_IP, "10.10.10.200")
            intent.putExtra(MergerService.INTENT_MERGER_PORT, 49090)
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        bound = false
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, TestTransactionActivity::class.java))
        }
    }
}