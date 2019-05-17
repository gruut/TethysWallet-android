package com.veronnnetworks.veronnwallet.utils.ext

import android.util.Base64
import org.spongycastle.util.encoders.Hex
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

fun ByteArray.toSha256Hex(): ByteArray {
    return this.toSha256().encodeHex()
}

fun ByteArray.toSha256(): ByteArray {
    val md = MessageDigest.getInstance("SHA-256")
    md.update(this)
    return md.digest()
}

fun String.toSha256(): ByteArray {
    return this.toByteArray(Charsets.UTF_8).toSha256()
}

fun ByteArray.encodeHex(): ByteArray {
    return Hex.encode(this)
}

fun ByteArray.decodeHex(): ByteArray {
    return Hex.decode(this)
}