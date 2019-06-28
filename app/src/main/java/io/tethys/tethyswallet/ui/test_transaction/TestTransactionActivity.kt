package io.tethys.tethyswallet.ui.test_transaction

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import io.tethys.tethyswallet.R
import io.tethys.tethyswallet.databinding.ActivityTestTransactionBinding
import io.tethys.tethyswallet.service.TestTransactionService
import io.tethys.tethyswallet.ui.NavigationController
import io.tethys.tethyswallet.ui.common.activity.BaseActivity
import kotlinx.android.synthetic.main.activity_test_transaction.*
import kotlinx.android.synthetic.main.activity_test_transaction.view.*
import javax.inject.Inject

class TestTransactionActivity : BaseActivity() {
    @Inject
    lateinit var navigationController: NavigationController

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

            TestTransactionService.send(ipAddress, port)
        }
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, TestTransactionActivity::class.java))
        }
    }
}