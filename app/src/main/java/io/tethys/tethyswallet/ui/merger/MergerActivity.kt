package io.tethys.tethyswallet.ui.merger

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import io.tethys.tethyswallet.R
import io.tethys.tethyswallet.databinding.ActivityMergerBinding
import io.tethys.tethyswallet.ui.NavigationController
import io.tethys.tethyswallet.ui.common.activity.BaseActivity
import javax.inject.Inject

class MergerActivity : BaseActivity() {

    @Inject
    lateinit var navigationController: NavigationController

    private val binding: ActivityMergerBinding by lazy {
        DataBindingUtil
            .setContentView<ActivityMergerBinding>(this, R.layout.activity_merger)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.nav_item_merger)

        navigationController.navigateToMerger()
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, MergerActivity::class.java))
        }
    }
}
