package com.veronnnetworks.veronnwallet.auth

import com.veronnnetworks.veronnwallet.data.local.PreferenceHelper
import com.veronnnetworks.veronnwallet.utils.CryptoConstants.ALIAS_VERONN
import com.veronnnetworks.veronnwallet.utils.CryptoConstants.CURVE_SECP256R1
import com.veronnnetworks.veronnwallet.utils.CryptoConstants.KEYSTORE_PROVIDER_ANDROID_KEYSTORE
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
import org.spongycastle.asn1.sec.SECObjectIdentifiers
import org.spongycastle.asn1.x500.X500Name
import org.spongycastle.asn1.x500.X500NameBuilder
import org.spongycastle.asn1.x500.style.BCStyle
import org.spongycastle.asn1.x509.AlgorithmIdentifier
import org.spongycastle.crypto.util.PublicKeyFactory
import org.spongycastle.crypto.util.SubjectPublicKeyInfoFactory
import org.spongycastle.jce.provider.BouncyCastleProvider
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.SecureRandom
import java.security.Security
import java.security.cert.X509Certificate
import java.security.spec.ECGenParameterSpec
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
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
            with(
                KeyPairGenerator.getInstance("ECDH", "SC")
            ) {
                val paramSpec = ECGenParameterSpec(CURVE_SECP256R1)
                initialize(paramSpec)
                generateKeyPair().apply {
                    preferenceHelper.ecPublicKey = public.encoded
                    preferenceHelper.ecSecretKey = private.encoded
                    preferenceHelper.commonName =
                        public.encoded.toSha256Hex().encodeToBase58String()
                }
            }
        }.subscribeOn(schedulerProvider.io())
    }

    override fun generateCSRPem(): Single<String> {
        return Single.fromCallable {
            val x500Name = X500NameBuilder(X500Name.getDefaultStyle())
                .addRDN(BCStyle.CN, preferenceHelper.commonName)
                .build()

            val pubKeyParam = PublicKeyFactory.createKey(preferenceHelper.ecPublicKey)
            val pubKeyInfo = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(pubKeyParam)
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
            val salt = ByteArray(8)
            SecureRandom().nextBytes(salt)

            val pbeKeySpec = PBEKeySpec(password.toCharArray(), salt, 10000, 32 * 8)
            val pbeKey =
                SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(pbeKeySpec)

            val cipher = Cipher.getInstance("AES/GCM/NoPadding").apply {
                init(Cipher.ENCRYPT_MODE, pbeKey)
            }
            cipher.doFinal(preferenceHelper.ecSecretKey).toPemString(PemType.PRIVATE_KEY)
        }.subscribeOn(schedulerProvider.io())
    }
}