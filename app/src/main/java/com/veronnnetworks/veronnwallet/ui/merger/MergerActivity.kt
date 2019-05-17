package com.veronnnetworks.veronnwallet.ui.merger

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.veronnnetworks.veronnwallet.R
import com.veronnnetworks.veronnwallet.ui.NavigationController
import com.veronnnetworks.veronnwallet.ui.common.activity.BaseActivity
import javax.inject.Inject

class MergerActivity : BaseActivity() {

    @Inject
    lateinit var navigationController: NavigationController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_merger)

        navigationController.navigateToMerger()
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, MergerActivity::class.java))
        }
    }
}
