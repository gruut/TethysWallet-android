package com.veronnnetworks.veronnwallet.auth

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.veronnnetworks.veronnwallet.utils.CryptoConstants.ALIAS_LOCAL
import com.veronnnetworks.veronnwallet.utils.CryptoConstants.KEYSTORE_PROVIDER_ANDROID_KEYSTORE
import com.veronnnetworks.veronnwallet.utils.CryptoConstants.RSA_ECB_PKCS1Padding
import com.veronnnetworks.veronnwallet.utils.ext.fromBase64
import com.veronnnetworks.veronnwallet.utils.ext.toBase64
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.spec.RSAKeyGenParameterSpec
import java.security.spec.RSAKeyGenParameterSpec.F4
import javax.crypto.Cipher

object LocalKeyStoreHelper {
    /** All inputs are must be shorter than 2048 bits(256 bytes) */
    private const val KEY_LENGTH_BIT = 2048
    private var keyEntry: KeyStore.Entry

    init {
        val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE).apply { load(null) }

        if (!keyStore.isKeyEntry(ALIAS_LOCAL)) {
            generateRSAKeyPair()
        }
        this.keyEntry = keyStore.getEntry(ALIAS_LOCAL, null)
    }

    private fun generateRSAKeyPair() {
        with(
            KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_RSA,
                KEYSTORE_PROVIDER_ANDROID_KEYSTORE
            )
        ) {
            val spec = KeyGenParameterSpec.Builder(
                ALIAS_LOCAL, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            ).setAlgorithmParameterSpec(RSAKeyGenParameterSpec(KEY_LENGTH_BIT, F4))
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                .setDigests(
                    KeyProperties.DIGEST_SHA256,
                    KeyProperties.DIGEST_SHA384,
                    KeyProperties.DIGEST_SHA512
                )
                .setUserAuthenticationRequired(false)
                .build()

            initialize(spec)
            generateKeyPair()
        }
    }

    fun encrypt(plainText: String): String {
        val cipher = Cipher
            .getInstance(RSA_ECB_PKCS1Padding).apply {
                init(
                    Cipher.ENCRYPT_MODE,
                    (keyEntry as KeyStore.PrivateKeyEntry).certificate.publicKey
                )
            }

        val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        return encryptedBytes.toBase64()
    }

    fun decrypt(base64EncodedCipherText: String): String {
        val cipher = Cipher
            .getInstance(RSA_ECB_PKCS1Padding).apply {
                init(Cipher.DECRYPT_MODE, (keyEntry as KeyStore.PrivateKeyEntry).privateKey)
            }

        val encryptedBytes = base64EncodedCipherText.fromBase64()
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes)
    }
}