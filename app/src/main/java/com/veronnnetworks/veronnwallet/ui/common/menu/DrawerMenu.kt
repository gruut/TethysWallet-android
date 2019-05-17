package com.veronnnetworks.veronnwallet.ui.common.menu

import androidx.annotation.IdRes
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.veronnnetworks.veronnwallet.R
import com.veronnnetworks.veronnwallet.data.local.PreferenceHelper
import com.veronnnetworks.veronnwallet.ui.NavigationController
import com.veronnnetworks.veronnwallet.ui.join.JoinActivity
import com.veronnnetworks.veronnwallet.ui.main.MainActivity
import com.veronnnetworks.veronnwallet.ui.merger.MergerActivity
import javax.inject.Inject
import kotlin.reflect.KClass

class DrawerMenu @Inject constructor(
    private val activity: AppCompatActivity,
    private val navigationController: NavigationController,
    private val preferenceHelper: PreferenceHelper
) {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var currentNavigationItem: DrawerNavigationItem

    fun setup(
        drawerLayout: DrawerLayout,
        navigationView: NavigationView,
        toolbar: Toolbar? = null,
        actionBarDrawerSync: Boolean = false
    ) {
        this.drawerLayout = drawerLayout
        if (actionBarDrawerSync) {
            object : ActionBarDrawerToggle(
                activity,
                drawerLayout,
                toolbar,
                R.string.nav_content_description_drawer_open,
                R.string.nav_content_description_drawer_close
            ) {
            }.also {
                drawerLayout.addDrawerListener(it)
            }.apply {
                isDrawerIndicatorEnabled = true
                isDrawerSlideAnimationEnabled = false
                syncState()
            }
        }

        navigationView.inflateMenu(R.menu.anonym_drawer)
        if (preferenceHelper.isAutonym) {
            navigationView.menu.clear()
            navigationView.inflateMenu(R.menu.autonym_drawer)
        }

        navigationView.setNavigationItemSelectedListener { item ->
            DrawerNavigationItem
                .values()
                .first { it.menuId == item.itemId }
                .apply {
                    if (this != currentNavigationItem) {
                        navigate(navigationController)
                    }
                }
            drawerLayout.closeDrawers()
            false
        }

        currentNavigationItem = DrawerNavigationItem
            .values()
            .firstOrNull { activity::class == it.activityClass }
            ?.also {
                navigationView.setCheckedItem(it.menuId)
            }
            ?: DrawerNavigationItem.OTHER
    }

    fun closeDrawerIfNeeded(): Boolean {
        return if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers()
            false
        } else {
            true
        }
    }

    enum class DrawerNavigationItem(
        @IdRes val menuId: Int,
        val activityClass: KClass<*>,
        val navigate: NavigationController.() -> Unit
    ) {
        HOME(R.id.nav_item_home, MainActivity::class, {
            navigateToMainActivity()
        }),
        MERGER(R.id.nav_item_merger, MergerActivity::class, {
            navigateToMergerActivity()
        }),
        AUTONYM(R.id.nav_item_autonym, JoinActivity::class, {
            navigateToJoinFromDrawer()
        }),
        OTHER(0, Unit::class, {
            // Do Nothing
        })
    }
}