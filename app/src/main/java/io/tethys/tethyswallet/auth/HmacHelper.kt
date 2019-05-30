package io.tethys.tethyswallet.auth

import io.tethys.tethyswallet.utils.CryptoConstants.HMAC_SHA256
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object HmacHelper {
    fun getHmacSignature(key: ByteArray, data: ByteArray): ByteArray =
        with(Mac.getInstance(HMAC_SHA256)) {
            init(SecretKeySpec(key, HMAC_SHA256))
            doFinal(data)
        }

    fun verifyHmacSignature(key: ByteArray, data: ByteArray, hmac: ByteArray): Boolean =
        Arrays.equals(hmac, getHmacSignature(key, data))
}