package io.tethys.tethyswallet.ui.common.fragment

import android.content.Context
import android.os.Bundle
import io.tethys.tethyswallet.ui.common.activity.BaseActivity
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.DaggerFragment


abstract class BaseFragment : DaggerFragment(), IOnBackPressed {

    var activity: BaseActivity? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity) {
            activity = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        performDependencyInjection()
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onDetach() {
        activity = null
        super.onDetach()
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    fun hideKeyboard() {
        activity?.hideKeyboard()
    }

    fun isNetworkConnected(): Boolean {
        return activity != null && activity!!.isNetworkConnected()
    }

    private fun performDependencyInjection() {
        AndroidSupportInjection.inject(this)
    }
}