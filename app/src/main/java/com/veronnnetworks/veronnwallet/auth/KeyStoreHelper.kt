package com.veronnnetworks.veronnwallet.auth

import io.reactivex.Completable
import io.reactivex.Single

interface KeyStoreHelper {
    fun isECKeyPairExist(): Boolean
    fun generateECKeyPair(): Completable
    fun generateCSRPem(): Single<String>
    fun putCertificatePem(pemCert: String): Single<Unit>
    fun getCertificatePem(): Single<String>
    fun getEncryptedSecretKeyPem(password: String): Single<String>
}