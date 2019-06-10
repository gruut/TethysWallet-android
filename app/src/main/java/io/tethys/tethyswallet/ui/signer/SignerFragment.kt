package io.tethys.tethyswallet.ui.signer

import android.content.Context
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import io.tethys.tethyswallet.R
import io.tethys.tethyswallet.ui.common.fragment.IOnBackPressed
import io.tethys.tethyswallet.utils.AppConstants

class SignerFragment : PreferenceFragmentCompat(), IOnBackPressed {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = AppConstants.PREF_NAME
        preferenceManager.sharedPreferencesMode = Context.MODE_PRIVATE

        setPreferencesFromResource(R.xml.signer_setup_pref, rootKey)
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    companion object {
        fun newInstance(): SignerFragment = SignerFragment()
    }
}
