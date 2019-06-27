package io.tethys.tethyswallet.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import io.tethys.tethyswallet.R
import io.tethys.tethyswallet.ui.common.fragment.Findable
import io.tethys.tethyswallet.ui.join.JoinActivity
import io.tethys.tethyswallet.ui.join.JoinAutonymFragment
import io.tethys.tethyswallet.ui.join.JoinFragment
import io.tethys.tethyswallet.ui.main.MainActivity
import io.tethys.tethyswallet.ui.merger.MergerActivity
import io.tethys.tethyswallet.ui.merger.MergerFragment
import io.tethys.tethyswallet.ui.signer.SignerActivity
import io.tethys.tethyswallet.ui.signer.SignerFragment
import io.tethys.tethyswallet.ui.test_transaction.TestTransactionActivity
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

    fun navigateToSignerActivity() {
        SignerActivity.start(activity)
    }

    fun navigateToTestTransactionActivity() {
        TestTransactionActivity.start(activity)
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

    fun navigateToSigner() {
        replaceFragment(SignerFragment.newInstance())
    }
}