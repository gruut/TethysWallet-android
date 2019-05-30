package io.tethys.tethyswallet.ui.main

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.databinding.DataBindingUtil
import io.tethys.tethyswallet.R
import io.tethys.tethyswallet.databinding.ActivityMainBinding
import io.tethys.tethyswallet.databinding.DrawerHeaderBinding
import io.tethys.tethyswallet.service.MergerService
import io.tethys.tethyswallet.ui.NavigationController
import io.tethys.tethyswallet.ui.common.activity.BaseActivity
import io.tethys.tethyswallet.ui.common.menu.DrawerMenu
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @Inject
    lateinit var navigationController: NavigationController
    @Inject
    lateinit var drawerMenu: DrawerMenu

    private val binding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }

    private val navBinding: DrawerHeaderBinding by lazy {
        DrawerHeaderBinding.bind(binding.drawer.getHeaderView(0))
    }

    private lateinit var mergerService: MergerService
    private var bound: Boolean = false
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MergerService.MergerBinder
            mergerService = binder.getService()
            bound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)

        drawerMenu.setup(binding.drawerLayout, binding.drawer, binding.toolbar, true)
    }

    override fun onStart() {
        super.onStart()
        Intent(this, MergerService::class.java).also { intent ->
            intent.putExtra(MergerService.INTENT_MERGER_IP, "10.10.10.112")
            intent.putExtra(MergerService.INTENT_MERGER_PORT, 8089)
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        bound = false
    }

    override fun onBackPressed() {
        if (drawerMenu.closeDrawerIfNeeded()) {
            super.onBackPressed()
        }
    }

    companion object {
        fun createIntent(context: Context): Intent = Intent(context, MainActivity::class.java)

        fun start(context: Context) {
            createIntent(context).let {
                it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(it)
            }
        }
    }
}
