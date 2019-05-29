package com.veronnnetworks.veronnwallet.data.local

import android.content.Context
import com.veronnnetworks.veronnwallet.auth.LocalKeyStoreHelper
import com.veronnnetworks.veronnwallet.di.PreferenceInfo
import com.veronnnetworks.veronnwallet.utils.ext.fromBase64
import com.veronnnetworks.veronnwallet.utils.ext.toBase64
import javax.inject.Inject

class AppPreferenceHelper @Inject constructor(
    val context: Context,
    @PreferenceInfo val prefFileName: String
) : PreferenceHelper {
    private val PREF_KEY_COMMON_NAME: String = "PREF_KEY_COMMON_NAME"
    private val PREF_KEY_PK: String = "PREF_KEY_PK"
    private val PREF_KEY_ENCRYPTED_SK: String = "PREF_KEY_ENCRYPTED_SK"
    private val PREF_KEY_IS_AUTONYM: String = "PREF_KEY_IS_AUTONYM"

    private val prefs = context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE)

    override var commonName: String?
        get() = prefs.getString(PREF_KEY_COMMON_NAME, null)
        set(value) = prefs.edit().putString(PREF_KEY_COMMON_NAME, value).apply()

    override var ecPublicKey: ByteArray?
        get() = prefs.getString(PREF_KEY_PK, null)?.let { it.fromBase64() }
        set(value) = prefs.edit().run {
            putString(
                PREF_KEY_PK,
                value.toBase64()
            ).apply()
        }

    override var ecSecretKey: ByteArray?
        get() {
            return if (getSecurely(PREF_KEY_ENCRYPTED_SK, "").isEmpty()) null
            else getSecurely(PREF_KEY_ENCRYPTED_SK, "").fromBase64()
        }
        set(value) = putSecurely(PREF_KEY_ENCRYPTED_SK, value.toBase64())

    override var isAutonym: Boolean
        get() = prefs.getBoolean(PREF_KEY_IS_AUTONYM, false)
        set(value) = prefs.edit().putBoolean(PREF_KEY_IS_AUTONYM, value).apply()

    private fun putSecurely(key: String, value: Any?) {
        prefs.edit().run {
            if (value == null) {
                remove(key)
            } else {
                when (value) {
                    is ByteArray -> putString(key, LocalKeyStoreHelper.encrypt(String(value)))
                    else -> putString(key, LocalKeyStoreHelper.encrypt(value.toString()))
                }
            }
            apply()
        }
    }

    private fun <T : Any> getSecurely(key: String, defaultValue: T): T {
        val str = prefs.getString(key, "")
        if (str.isNullOrEmpty()) return defaultValue

        val value = LocalKeyStoreHelper.decrypt(str)

        @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
        return when (defaultValue) {
            is String -> value
            is Boolean -> value.toBoolean()
            is Int -> value.toInt()
            is Long -> value.toLong()
            else -> throw IllegalArgumentException("Wrong default type")
        } as T
    }
}