package io.tethys.tethyswallet.utils.ext

import android.util.Base64
import io.tethys.tethyswallet.utils.CryptoConstants
import org.spongycastle.jce.ECNamedCurveTable
import org.spongycastle.jce.provider.BouncyCastleProvider
import org.spongycastle.jce.spec.ECPrivateKeySpec
import org.spongycastle.jce.spec.ECPublicKeySpec
import org.spongycastle.util.encoders.Hex
import java.math.BigInteger
import java.security.*

fun ByteArray.toSha256Hex(): String {
    return this.toSha256().encodeHex().toString()
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

fun getTimestamp() = (System.currentTimeMillis() / 1000).toInt()

fun getNonce(bitSize: Int = 256): String {
    val nonce = ByteArray(bitSize / 8)
    SecureRandom().nextBytes(nonce)
    return nonce.toBase64()
}

fun ByteArray?.toBase64(): String = Base64.encodeToString(this, Base64.NO_WRAP)
fun String.fromBase64(): ByteArray = Base64.decode(this.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)

fun ByteArray?.hexToPrivateKey(): PrivateKey {
    this ?: throw IllegalArgumentException("Illegal private key format")

    Security.insertProviderAt(BouncyCastleProvider(), 1)
    val ecParameterSpec = ECNamedCurveTable.getParameterSpec(CryptoConstants.CURVE_SECP256R1)
    val bigInt = BigInteger(String(this), 16)

    val prvKeySpec = ECPrivateKeySpec(bigInt, ecParameterSpec)
    return KeyFactory.getInstance("ECDH", "SC").generatePrivate(prvKeySpec)
}

fun ByteArray?.hexToPublicKey(): PublicKey {
    this ?: throw IllegalArgumentException("Illegal public key format")

    Security.insertProviderAt(BouncyCastleProvider(), 1)
    val ecParameterSpec = ECNamedCurveTable.getParameterSpec(CryptoConstants.CURVE_SECP256R1)
    val curve = ecParameterSpec.curve
    val point = curve.decodePoint(Hex.decode(this))

    val pubKeySpec = ECPublicKeySpec(point, ecParameterSpec)
    return KeyFactory.getInstance("ECDH", "SC").generatePublic(pubKeySpec)
}

fun ByteArray?.hexToPointPair(): Pair<String, String> {
    this ?: throw IllegalArgumentException("Illegal point format")

    val ecParameterSpec = ECNamedCurveTable.getParameterSpec(CryptoConstants.CURVE_SECP256R1)
    val curve = ecParameterSpec.curve
    val point = curve.decodePoint(Hex.decode(this))

    return Pair(point.affineXCoord.toString(), point.affineYCoord.toString())
}

fun Pair<ByteArray, ByteArray>.pointPairToHex(): ByteArray =
    "04".toByteArray(Charsets.UTF_8) + first + second