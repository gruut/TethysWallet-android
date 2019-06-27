package io.tethys.tethyswallet.ui.test_transaction

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import io.tethys.tethyswallet.R
import io.tethys.tethyswallet.databinding.ActivityTestTransactionBinding
import io.tethys.tethyswallet.ui.NavigationController
import io.tethys.tethyswallet.ui.common.activity.BaseActivity
import javax.inject.Inject

class TestTransactionActivity : BaseActivity() {
    @Inject
    lateinit var navigationController: NavigationController

    private val binding: ActivityTestTransactionBinding by lazy {
        DataBindingUtil
            .setContentView<ActivityTestTransactionBinding>(this, R.layout.activity_test_transaction)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.nav_item_test_transaction)

        setContentView(R.layout.activity_test_transaction)
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, TestTransactionActivity::class.java))
        }
    }
}