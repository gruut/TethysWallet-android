package io.tethys.tethyswallet.auth

import io.tethys.tethyswallet.utils.CryptoConstants
import io.tethys.tethyswallet.utils.ext.encodeHex
import io.tethys.tethyswallet.utils.ext.hexToPrivateKey
import io.tethys.tethyswallet.utils.ext.hexToPublicKey
import io.tethys.tethyswallet.utils.ext.toBase64
import org.spongycastle.asn1.sec.SECNamedCurves
import org.spongycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey
import org.spongycastle.jce.interfaces.ECPublicKey
import org.spongycastle.math.ec.ECPoint
import java.math.BigInteger
import java.security.MessageDigest
import java.security.SecureRandom

object SignatureHelper {
    fun sign(prvKey: ByteArray, pubKey: ByteArray, data: ByteArray): String {
        val privateKey = prvKey.hexToPrivateKey() as BCECPrivateKey
        val publicKey = pubKey.hexToPublicKey() as ECPublicKey

        val curve = SECNamedCurves.getByName(CryptoConstants.CURVE_SECP256K1)
        val q = curve.n
        val r = BigInteger(q.bitLength(), SecureRandom()) //  1 ~ q 사이의 Random Big Integer
        val a = curve.g.multiply(r)
        val d = hashPoint(a)
        val e = hashPointMsg(publicKey.q, data)
        val z = (r.multiply(d) - e.multiply(privateKey.d)).mod(q)

        return (d.toString(16) + z.toString(16)).toByteArray().toBase64()
    }

    private fun hashPoint(point: ECPoint): BigInteger {
        val md = MessageDigest.getInstance("SHA-256")

        md.update(String(point.xCoord.encoded.encodeHex()).toUpperCase().toByteArray())
        md.update(String(point.yCoord.encoded.encodeHex()).toUpperCase().toByteArray())

        val hash = md.digest().encodeHex()
        return BigInteger(String(hash), 16)
    }

    private fun hashPointMsg(point: ECPoint, msg: ByteArray): BigInteger {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(String(point.xCoord.encoded.encodeHex()).toUpperCase().toByteArray())
        md.update(String(point.yCoord.encoded.encodeHex()).toUpperCase().toByteArray())
        md.update(msg)

        val hash = md.digest().encodeHex()
        return BigInteger(String(hash), 16)
    }
}