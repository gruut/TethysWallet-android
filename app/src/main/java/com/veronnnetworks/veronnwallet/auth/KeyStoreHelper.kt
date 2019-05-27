package com.veronnnetworks.veronnwallet.auth

import io.reactivex.Completable
import io.reactivex.Single
import org.spongycastle.math.ec.ECPoint
import java.security.PrivateKey
import java.security.PublicKey

interface KeyStoreHelper {
    fun isECKeyPairExist(): Boolean
    fun generateECKeyPair(): Completable
    fun generateCSRPem(): Single<String>
    fun putCertificatePem(pemCert: String): Single<Unit>
    fun getCertificatePem(): Single<String>
    fun getEncryptedSecretKeyPem(password: String): Single<String>
    fun getSharedSecretKey(othersPubKey: PublicKey): Single<ByteArray>
    fun signWithECKey(data: ByteArray): Single<String>
    fun verifyWithCert(data: ByteArray, signature: String, cert: String): Single<Boolean>
}