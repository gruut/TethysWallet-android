package io.tethys.tethyswallet.ui.join

import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.tethys.tethyswallet.R
import io.tethys.tethyswallet.ui.NavigationController
import io.tethys.tethyswallet.ui.common.activity.BaseActivity
import javax.inject.Inject

class JoinActivity : BaseActivity() {

    @Inject
    lateinit var navigationController: NavigationController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        if (!intent.getBooleanExtra(EXTRA_FROM_DRAWER, false)) {
            navigationController.navigateToJoin()
        } else {
            navigationController.navigateToJoinAutonym(true)
        }
    }

    companion object {
        const val EXTRA_FROM_DRAWER = "EXTRA_FROM_DRAWER"

        fun createIntent(context: Context, isFromDrawer: Boolean): Intent {
            return Intent(context, JoinActivity::class.java).apply {
                putExtra(EXTRA_FROM_DRAWER, isFromDrawer)
            }
        }

        fun start(context: Context, isFromDrawer: Boolean) {
            context.startActivity(createIntent(context, isFromDrawer))
        }

        fun start(context: Context) {
            context.startActivity(Intent(context, JoinActivity::class.java))
        }
    }
}
