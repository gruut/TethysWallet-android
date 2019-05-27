package com.veronnnetworks.veronnwallet.utils.ext

import org.spongycastle.openssl.PKCS8Generator
import org.spongycastle.openssl.jcajce.JcaPKCS8Generator
import org.spongycastle.openssl.jcajce.JceOpenSSLPKCS8EncryptorBuilder
import org.spongycastle.util.io.pem.PemObject
import org.spongycastle.util.io.pem.PemWriter
import java.io.ByteArrayInputStream
import java.io.StringWriter
import java.security.PrivateKey
import java.security.SecureRandom
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

enum class PemType(val value: String) {
    CERTIFICATE_REQUEST("CERTIFICATE REQUEST"),
    CERTIFICATE("CERTIFICATE"),
    ENCRYPTED_PRIVATE_KEY("ENCRYPTED PRIVATE KEY")
}

fun ByteArray.toPemString(type: PemType): String {
    val pemObject = PemObject(type.value, this)
    val stringWriter = StringWriter()
    val pemWriter = PemWriter(stringWriter)
    pemWriter.writeObject(pemObject)
    pemWriter.close()
    stringWriter.close()

    return stringWriter.toString()
}

fun PrivateKey.toPemString(password: String): String {
    JceOpenSSLPKCS8EncryptorBuilder(PKCS8Generator.AES_128_CBC)
        .setRandom(SecureRandom())
        .setPasssword(password.toCharArray())
        .build()
        .apply {
            val pemObject = JcaPKCS8Generator(this@toPemString, this).generate()
            val stringWriter = StringWriter()
            val pemWriter = PemWriter(stringWriter)
            pemWriter.writeObject(pemObject)
            pemWriter.close()
            stringWriter.close()

            return stringWriter.toString()
        }
}

fun String.toX509Cert(): X509Certificate? {
    if (this.trim().isNotEmpty()) {
        val input = this.replace("-----BEGIN CERTIFICATE-----", "")
            .replace("-----END CERTIFICATE-----", "")

        val cf = CertificateFactory.getInstance("X509", "BC")
        return cf.generateCertificate(
            ByteArrayInputStream(input.fromBase64())
        ) as X509Certificate
    }
    return null
}