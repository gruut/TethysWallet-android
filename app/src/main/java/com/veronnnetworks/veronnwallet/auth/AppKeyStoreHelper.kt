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
import org.spongycastle.jce.ECNamedCurveTable
import org.spongycastle.jce.provider.BouncyCastleProvider
import org.spongycastle.jce.spec.ECPrivateKeySpec
import org.spongycastle.jce.spec.ECPublicKeySpec
import org.spongycastle.util.encoders.Hex
import java.math.BigInteger
import java.security.*
import java.security.cert.X509Certificate
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
                            .toSha256Hex()
                            .encodeToBase58String()
                }
            }
        }.subscribeOn(schedulerProvider.io())
    }

    private fun hexToPublicKey(hexPub: ByteArray?): PublicKey {
        hexPub ?: throw IllegalArgumentException("Illegal public key format")

        Security.insertProviderAt(BouncyCastleProvider(), 1)
        val ecParameterSpec = ECNamedCurveTable.getParameterSpec(CURVE_SECP256R1)
        val curve = ecParameterSpec.curve
        val point = curve.decodePoint(Hex.decode(hexPub))

        val pubKeySpec = ECPublicKeySpec(point, ecParameterSpec)
        return KeyFactory.getInstance("ECDH", "SC").generatePublic(pubKeySpec)
    }

    private fun hexToPrivateKey(hexPrv: ByteArray?): PrivateKey {
        hexPrv ?: throw IllegalArgumentException("Illegal public key format")

        Security.insertProviderAt(BouncyCastleProvider(), 1)
        val ecParameterSpec = ECNamedCurveTable.getParameterSpec(CURVE_SECP256R1)
        val bigInt = BigInteger(String(hexPrv), 16)

        val prvKeySpec = ECPrivateKeySpec(bigInt, ecParameterSpec)
        return KeyFactory.getInstance("ECDH", "SC").generatePrivate(prvKeySpec)
    }

    override fun generateCSRPem(): Single<String> {
        return Single.fromCallable {
            val x500Name = X500NameBuilder(X500Name.getDefaultStyle())
                .addRDN(BCStyle.CN, preferenceHelper.commonName)
                .build()

            val pubKeyInfo =
                SubjectPublicKeyInfo.getInstance(hexToPublicKey(preferenceHelper.ecPublicKey).encoded)
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