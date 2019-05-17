package com.veronnnetworks.veronnwallet.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

class NetworkUtils {
    companion object {
        fun isNetworkConnected(context: Context): Boolean {
            val cm: ConnectivityManager? =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (cm != null) {
                val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
                return activeNetwork != null && activeNetwork.isConnected
            }
            return false
        }
    }
}