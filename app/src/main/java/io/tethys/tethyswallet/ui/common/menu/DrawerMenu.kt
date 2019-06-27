package io.tethys.tethyswallet.ui.common.menu

import androidx.annotation.IdRes
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import io.tethys.tethyswallet.R
import io.tethys.tethyswallet.data.local.PreferenceHelper
import io.tethys.tethyswallet.ui.BaseApp
import io.tethys.tethyswallet.ui.NavigationController
import io.tethys.tethyswallet.ui.join.JoinActivity
import io.tethys.tethyswallet.ui.main.MainActivity
import io.tethys.tethyswallet.ui.merger.MergerActivity
import io.tethys.tethyswallet.ui.test_transaction.TestTransactionActivity
import javax.inject.Inject
import kotlin.reflect.KClass

class DrawerMenu @Inject constructor(
    private val activity: AppCompatActivity,
    private val navigationController: NavigationController
) {
    private val prefHelper: PreferenceHelper = BaseApp.prefHelper
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
        if (prefHelper.isAutonym) {
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
        SIGNER(R.id.nav_item_signer, Unit::class, {
            navigateToSignerActivity()
        }),
        TEST_TRANSACTION(R.id.nav_item_test_transaction, TestTransactionActivity::class, {
            navigateToTestTransactionActivity()
        }),
        AUTONYM(R.id.nav_item_autonym, JoinActivity::class, {
            navigateToJoinFromDrawer()
        }),
        OTHER(0, Unit::class, {
            // Do Nothing
        })
    }
}