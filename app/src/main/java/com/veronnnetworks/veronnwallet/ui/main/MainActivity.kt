package com.veronnnetworks.veronnwallet.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.veronnnetworks.veronnwallet.R
import com.veronnnetworks.veronnwallet.databinding.ActivityMainBinding
import com.veronnnetworks.veronnwallet.databinding.DrawerHeaderBinding
import com.veronnnetworks.veronnwallet.ui.NavigationController
import com.veronnnetworks.veronnwallet.ui.common.activity.BaseActivity
import com.veronnnetworks.veronnwallet.ui.common.menu.DrawerMenu
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)

        drawerMenu.setup(binding.drawerLayout, binding.drawer, binding.toolbar, true)
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
