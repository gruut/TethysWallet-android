package com.veronnnetworks.veronnwallet.ui.common.databinding

import android.text.TextUtils
import android.util.Patterns
import androidx.core.text.isDigitsOnly

// TODO 타입 별 규칙 정의
enum class InputRule {
    NON_NULL_RULE {
        override fun valid(s: String?): Boolean {
            return !TextUtils.isEmpty(s)
        }
    },
    IP_RULE {
        override fun valid(s: String?): Boolean {
            return !TextUtils.isEmpty(s) && Patterns.IP_ADDRESS.matcher(s).matches()
        }
    },
    PORT_RULE {
        override fun valid(s: String?): Boolean {
            return !TextUtils.isEmpty(s) && s?.isDigitsOnly() ?: false
        }
    },
    PASSWORD_RULE {
        override fun valid(s: String?): Boolean {
            return !TextUtils.isEmpty(s) && s?.let { it.length > 14 } ?: false
        }

    };

    abstract fun valid(s: String?): Boolean
}