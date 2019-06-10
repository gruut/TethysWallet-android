package io.tethys.tethyswallet.data.local

import android.content.Context
import io.tethys.tethyswallet.auth.LocalKeyStoreHelper
import io.tethys.tethyswallet.utils.TethysConfigs
import io.tethys.tethyswallet.utils.ext.fromBase64
import io.tethys.tethyswallet.utils.ext.toBase64

class AppPreferenceHelper constructor(
    val context: Context,
    prefFileName: String
) : PreferenceHelper {
    private val PREF_KEY_COMMON_NAME: String = "PREF_KEY_COMMON_NAME"
    private val PREF_KEY_PK: String = "PREF_KEY_PK"
    private val PREF_KEY_ENCRYPTED_SK: String = "PREF_KEY_ENCRYPTED_SK"
    private val PREF_KEY_IS_AUTONYM: String = "PREF_KEY_IS_AUTONYM"
    private val PREF_KEY_IS_SIGNER: String = "PREF_KEY_IS_SIGNER"
    private val PREF_KEY_SIGNER_FOREGROUND: String = "PREF_KEY_SIGNER_FOREGROUND"
    private val PREF_KEY_WORLD_ID: String = "PREF_KEY_WORLD_ID"
    private val PREF_KEY_WORLD_CREATOR: String = "PREF_KEY_WORLD_CREATOR"
    private val PREF_KEY_CHAIN_ID: String = "PREF_KEY_CHAIN_ID"

    private val prefs = context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE)

    override var commonName: String?
        get() = prefs.getString(PREF_KEY_COMMON_NAME, null)
        set(value) = prefs.edit().putString(PREF_KEY_COMMON_NAME, value).apply()

    override var ecPublicKey: ByteArray?
        get() = prefs.getString(PREF_KEY_PK, null)?.fromBase64()
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

    override val isSigner: Boolean
        get() = prefs.getBoolean(PREF_KEY_IS_SIGNER, false)

    override val signerForeground: Boolean
        get() = prefs.getBoolean(PREF_KEY_SIGNER_FOREGROUND, false)

    override var worldId: String?
        get() = prefs.getString(PREF_KEY_WORLD_ID, TethysConfigs.TEST_WORLD_ID)
        set(value) = prefs.edit().putString(PREF_KEY_WORLD_ID, value).apply()

    override var worldCreator: String?
        get() = prefs.getString(PREF_KEY_WORLD_CREATOR, null)
        set(value) = prefs.edit().putString(PREF_KEY_WORLD_CREATOR, value).apply()

    override var chainId: String?
        get() = prefs.getString(PREF_KEY_CHAIN_ID, TethysConfigs.TEST_CHAIN_ID)
        set(value) = prefs.edit().putString(PREF_KEY_CHAIN_ID, value).apply()

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