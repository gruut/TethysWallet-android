package com.veronnnetworks.veronnwallet.ui.merger

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.veronnnetworks.veronnwallet.R
import com.veronnnetworks.veronnwallet.databinding.ActivityMergerBinding
import com.veronnnetworks.veronnwallet.ui.NavigationController
import com.veronnnetworks.veronnwallet.ui.common.activity.BaseActivity
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
