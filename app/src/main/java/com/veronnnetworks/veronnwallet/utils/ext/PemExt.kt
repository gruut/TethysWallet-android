package com.veronnnetworks.veronnwallet.utils.ext

import android.util.Base64
import org.spongycastle.util.io.pem.PemObject
import org.spongycastle.util.io.pem.PemWriter
import java.io.ByteArrayInputStream
import java.io.StringWriter
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

enum class PemType(val value: String) {
    CERTIFICATE_REQUEST("CERTIFICATE REQUEST"),
    CERTIFICATE("CERTIFICATE"),
    PRIVATE_KEY("PRIVATE KEY")
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

fun String.toX509Cert(): X509Certificate? {
    if (this.trim().isNotEmpty()) {
        val input = this.replace("-----BEGIN CERTIFICATE-----", "")
            .replace("-----END CERTIFICATE-----", "")

        val cf = CertificateFactory.getInstance("X509", "BC")
        return cf.generateCertificate(
            ByteArrayInputStream(
                Base64.decode(
                    input,
                    Base64.NO_WRAP
                )
            )
        ) as X509Certificate
    }
    return null
}