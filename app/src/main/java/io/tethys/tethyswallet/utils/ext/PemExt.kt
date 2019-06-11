package io.tethys.tethyswallet.utils.ext

import org.spongycastle.asn1.pkcs.PrivateKeyInfo
import org.spongycastle.jce.interfaces.ECPrivateKey
import org.spongycastle.openssl.PEMParser
import org.spongycastle.openssl.PKCS8Generator
import org.spongycastle.openssl.jcajce.JcaPEMKeyConverter
import org.spongycastle.openssl.jcajce.JcaPKCS8Generator
import org.spongycastle.openssl.jcajce.JceOpenSSLPKCS8EncryptorBuilder
import org.spongycastle.util.io.pem.PemObject
import org.spongycastle.util.io.pem.PemWriter
import java.io.ByteArrayInputStream
import java.io.StringReader
import java.io.StringWriter
import java.security.PrivateKey
import java.security.SecureRandom
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

enum class PemType(val value: String) {
    CERTIFICATE_REQUEST("CERTIFICATE REQUEST"),
    CERTIFICATE("CERTIFICATE"),
    ENCRYPTED_PRIVATE_KEY("ENCRYPTED PRIVATE KEY"),
    PUBLIC_KEY("PUBLIC KEY")
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

fun String.toPrivateKey(): ECPrivateKey? {
    val pemParser = PEMParser(StringReader(this))
    val pemObject = pemParser.readObject()
    val converter = JcaPEMKeyConverter().setProvider("SC")
    return if (pemObject is PrivateKeyInfo) {
        converter.getPrivateKey(pemObject) as ECPrivateKey?
    } else {
        null
    }
}