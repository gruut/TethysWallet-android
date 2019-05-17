package com.veronnnetworks.veronnwallet.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.veronnnetworks.veronnwallet.R
import com.veronnnetworks.veronnwallet.ui.common.fragment.Findable
import com.veronnnetworks.veronnwallet.ui.join.JoinActivity
import com.veronnnetworks.veronnwallet.ui.join.JoinAutonymFragment
import com.veronnnetworks.veronnwallet.ui.join.JoinFragment
import com.veronnnetworks.veronnwallet.ui.main.MainActivity
import com.veronnnetworks.veronnwallet.ui.merger.MergerActivity
import com.veronnnetworks.veronnwallet.ui.merger.MergerFragment
import javax.inject.Inject

class NavigationController @Inject constructor(
    private val activity: AppCompatActivity
) {
    private val containerId: Int = R.id.content
    private val fragmentManager: FragmentManager = activity.supportFragmentManager

    fun navigateToJoinActivity() {
        JoinActivity.start(activity)
    }

    fun navigateToJoinFromDrawer() {
        JoinActivity.start(activity, true)
    }

    fun navigateToMainActivity() {
        MainActivity.start(activity)
    }

    fun navigateToMergerActivity() {
        MergerActivity.start(activity)
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = fragmentManager
            .beginTransaction()
            .replace(containerId, fragment, null)

        if (fragmentManager.isStateSaved) {
            transaction.commitAllowingStateLoss()
        } else {
            transaction.commit()
        }
    }

    private fun addFragment(fragment: Fragment) {
        fragmentManager.popBackStack(
            (fragment as? Findable)?.tagForFinding,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )

        val transaction = fragmentManager
            .beginTransaction()
            .replace(containerId, fragment, (fragment as? Findable)?.tagForFinding)
            .addToBackStack((fragment as? Findable)?.tagForFinding)

        if (fragmentManager.isStateSaved) {
            transaction.commitAllowingStateLoss()
        } else {
            transaction.commit()
        }
    }

    fun navigateToJoinAutonym(fromDrawer: Boolean) {
        if (fromDrawer) {
            replaceFragment(JoinAutonymFragment.newInstance())
        } else {
            addFragment(JoinAutonymFragment.newInstance())
        }
    }

    fun navigateToJoin() {
        replaceFragment(JoinFragment.newInstance())
    }

    fun navigateToMerger() {
        replaceFragment(MergerFragment.newInstance())
    }
}