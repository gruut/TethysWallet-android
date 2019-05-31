package io.tethys.tethyswallet.ui.signer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import io.tethys.tethyswallet.R
import io.tethys.tethyswallet.databinding.ActivitySignerBinding
import io.tethys.tethyswallet.ui.NavigationController
import io.tethys.tethyswallet.ui.common.activity.BaseActivity
import javax.inject.Inject

class SignerActivity : BaseActivity() {

    @Inject
    lateinit var navigationController: NavigationController

    private val binding: ActivitySignerBinding by lazy {
        DataBindingUtil.setContentView<ActivitySignerBinding>(this, R.layout.activity_signer)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.nav_item_signer)

        navigationController.navigateToSigner()
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, SignerActivity::class.java))
        }
    }
}
