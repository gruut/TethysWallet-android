package com.veronnnetworks.veronnwallet.auth

import com.veronnnetworks.veronnwallet.data.local.PreferenceHelper
import com.veronnnetworks.veronnwallet.utils.CryptoConstants.ALIAS_VERONN
import com.veronnnetworks.veronnwallet.utils.CryptoConstants.CURVE_SECP256R1
import com.veronnnetworks.veronnwallet.utils.CryptoConstants.ECDH
import com.veronnnetworks.veronnwallet.utils.CryptoConstants.KEYSTORE_PROVIDER_ANDROID_KEYSTORE
import com.veronnnetworks.veronnwallet.utils.CryptoConstants.SHA256withECDSA
import com.veronnnetworks.veronnwallet.utils.ext.*
import com.veronnnetworks.veronnwallet.utils.rx.SchedulerProvider
import io.reactivex.Completable
import io.reactivex.Single
import org.spongycastle.asn1.DERBitString
import org.spongycastle.asn1.DERSet
import org.spongycastle.asn1.pkcs.Attribute
import org.spongycastle.asn1.pkcs.CertificationRequest
import org.spongycastle.asn1.pkcs.CertificationRequestInfo
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers
import org.spongycastle.asn1.sec.SECNamedCurves
import org.spongycastle.asn1.sec.SECObjectIdentifiers
import org.spongycastle.asn1.x500.X500Name
import org.spongycastle.asn1.x500.X500NameBuilder
import org.spongycastle.asn1.x500.style.BCStyle
import org.spongycastle.asn1.x509.AlgorithmIdentifier
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo
import org.spongycastle.crypto.generators.ECKeyPairGenerator
import org.spongycastle.crypto.params.ECDomainParameters
import org.spongycastle.crypto.params.ECKeyGenerationParameters
import org.spongycastle.crypto.params.ECPrivateKeyParameters
import org.spongycastle.crypto.params.ECPublicKeyParameters
import org.spongycastle.jce.provider.BouncyCastleProvider
import org.spongycastle.util.encoders.Hex
import java.security.*
import java.security.cert.X509Certificate
import javax.crypto.KeyAgreement
import javax.inject.Inject

class AppKeyStoreHelper @Inject constructor(
    private val preferenceHelper: PreferenceHelper,
    private val schedulerProvider: SchedulerProvider
) : KeyStoreHelper {
    override fun isECKeyPairExist(): Boolean {
        return preferenceHelper.ecPublicKey?.isNotEmpty() ?: false
                && preferenceHelper.ecSecretKey?.isNotEmpty() ?: false
    }

    override fun generateECKeyPair(): Completable {
        return Completable.fromAction {
            Security.insertProviderAt(BouncyCastleProvider(), 1)
            with(ECKeyPairGenerator()) {
                val x9ECParameters = SECNamedCurves.getByName(CURVE_SECP256R1)
                val params = ECDomainParameters(
                    x9ECParameters.curve,
                    x9ECParameters.g,
                    x9ECParameters.n,
                    x9ECParameters.h
                )
                val keyGenParam = ECKeyGenerationParameters(params, SecureRandom())
                init(keyGenParam)
                generateKeyPair().apply {
                    val privateKey = private as ECPrivateKeyParameters
                    preferenceHelper.ecSecretKey = Hex.encode(privateKey.d.toByteArray())

                    val publicKey = public as ECPublicKeyParameters
                    preferenceHelper.ecPublicKey = Hex.encode(publicKey.q.getEncoded(false))
                    preferenceHelper.commonName =
                        Hex.encode(publicKey.q.getEncoded(false))
                            .toSha256()
                            .encodeToBase58String()
                }
            }
        }.subscribeOn(schedulerProvider.io())
    }

    override fun generateCSRPem(): Single<String> {
        return Single.fromCallable {
            val x500Name = X500NameBuilder(X500Name.getDefaultStyle())
                .addRDN(BCStyle.CN, preferenceHelper.commonName)
                .build()

            val pubKeyInfo =
                SubjectPublicKeyInfo.getInstance(preferenceHelper.ecPublicKey.hexToPublicKey().encoded)
            val requestInfo = CertificationRequestInfo(x500Name, pubKeyInfo, null)

            val algorithmIdentifier = AlgorithmIdentifier(SECObjectIdentifiers.secp256r1)
            val attribute = Attribute(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest, DERSet())

            val csr =
                CertificationRequest(requestInfo, algorithmIdentifier, DERBitString(attribute))
            csr.encoded.toPemString(PemType.CERTIFICATE_REQUEST)
        }.subscribeOn(schedulerProvider.io())
    }

    override fun putCertificatePem(pemCert: String): Single<Unit> {
        return Single.fromCallable {
            val x509Cert = pemCert.toX509Cert()
                ?: throw TypeCastException("Input string is not x509 formatted string")
            KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE).apply {
                load(null)
                setCertificateEntry(ALIAS_VERONN, x509Cert)
            }

            preferenceHelper.isAutonym = true
            return@fromCallable
        }.subscribeOn(schedulerProvider.io())
    }

    override fun getCertificatePem(): Single<String> {
        return Single.fromCallable {
            val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE).apply {
                load(null)
            }
            (keyStore.getCertificate(ALIAS_VERONN) as X509Certificate).encoded.toPemString(PemType.CERTIFICATE)
        }.subscribeOn(schedulerProvider.io())
    }

    override fun getEncryptedSecretKeyPem(password: String): Single<String> {
        return Single.fromCallable {
            preferenceHelper.ecSecretKey.hexToPrivateKey().toPemString(password)
        }.subscribeOn(schedulerProvider.io())
    }

    override fun getSharedSecretKey(othersPubKey: PublicKey): Single<ByteArray> {
        return Single.fromCallable {
            val myPrvKey = preferenceHelper.ecSecretKey.hexToPrivateKey()
            with(KeyAgreement.getInstance(ECDH, "SC")) {
                init(myPrvKey)
                doPhase(othersPubKey, true)
                generateSecret().toSha256()
            }
        }.subscribeOn(schedulerProvider.io())
    }

    override fun signWithECKey(data: ByteArray): Single<String> =
        Single.fromCallable {
            val secretKey = preferenceHelper.ecSecretKey.hexToPrivateKey()
            with(Signature.getInstance(SHA256withECDSA)) {
                initSign(secretKey)
                update(data)
                sign().toBase64()
            }
        }

    override fun verifyWithCert(data: ByteArray, signature: String, cert: String): Single<Boolean> =
        Single.fromCallable {
            val cert = cert.toX509Cert()
            with(Signature.getInstance(SHA256withECDSA)) {
                initVerify(cert)
                update(data)
                verify(signature.fromBase64())
            }
        }
}