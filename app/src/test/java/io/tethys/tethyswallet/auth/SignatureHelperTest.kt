package io.tethys.tethyswallet.auth

import io.tethys.tethyswallet.utils.ext.encodeHex
import io.tethys.tethyswallet.utils.ext.toPrivateKey
import io.tethys.tethyswallet.utils.ext.toX509Cert
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.spongycastle.jce.provider.BouncyCastleProvider
import java.security.Security

@RunWith(RobolectricTestRunner::class)
class SignatureHelperTest {

    private val testSk: String = "-----BEGIN PRIVATE KEY-----\n" +
            "MIGEAgEAMBAGByqGSM49AgEGBSuBBAAKBG0wawIBAQQgBe+hD0dANH1UonOwQxqe\n" +
            "Fvc/J/C+8jTsqOtYSiQ+OamhRANCAAQ7U312c15p98EigrRri9+gp0BXvzSdhNUC\n" +
            "B2eSywdObeYu702dkOgHQ8rzpkUdj4TDMjItDR7fHshmfgaL3hWa\n" +
            "-----END PRIVATE KEY-----\n"
    private val testCert: String = "-----BEGIN CERTIFICATE-----\n" +
            "MIIC4zCCAUugAwIBAgIRAJbcGDlLWBv36VTBSifZ90owDQYJKoZIhvcNAQELBQAw\n" +
            "PTEVMBMGA1UEAxMMLy8vLy8vLy8vLzg9MQswCQYDVQQGEwJLUjEXMBUGA1UEChMO\n" +
            "R3J1dXQgTmV0d29ya3MwHhcNMTkwMjI2MDIzNDUyWhcNMjAwMjI2MDIzNDUyWjAR\n" +
            "MQ8wDQYDVQQDEwZURVNUQ04wVjAQBgcqhkjOPQIBBgUrgQQACgNCAAQ7U312c15p\n" +
            "98EigrRri9+gp0BXvzSdhNUCB2eSywdObeYu702dkOgHQ8rzpkUdj4TDMjItDR7f\n" +
            "HshmfgaL3hWao1gwVjAhBgNVHQ4EGgQY0wM+4GWLF2ySLHkLMoBzTbg3MuAzbuy3\n" +
            "MAwGA1UdEwEB/wQCMAAwIwYDVR0jBBwwGoAYGCaqNIlvb/99LRAlk5JGHBjYKebe\n" +
            "jLP9MA0GCSqGSIb3DQEBCwUAA4IBgQAU2HtzzCIqlh4DvHbtcH6duH/nAPEyXmk1\n" +
            "4NXFgbQQjQTlmRAHzpjXcRxaIjpesy6iOzTR7Rf5Oo1nDj9fXks8wMdTdruajqTv\n" +
            "7NA2Wd4d6qgM30i2ss/ebJm1pSTL04hQM6XvEvyvYt7lgVV/GXvzgUoW8GDXSw3X\n" +
            "3upTGlDJEuLlILzFskOBYReKXhen6WjEL1qecXw9FNHpvzuzRZdPUQkeJX9cZZJz\n" +
            "F1iT28uBYX3YFDGW4x2THGxZOqp3ssdvuC/oTerBdrUTr8JiYIoVjy42StzWI6aC\n" +
            "vyqJbkalyPt5YgVlPtFy+Adv+mcUpQ9i8sYlfE3iUKxeJnMKpGgvjg8ppThVpVBt\n" +
            "TikN67sThXbzdOcEBrp1HksShTYgDYQ0go7zOcrM/tZJoSOsGrYL465luqAADuJR\n" +
            "U6sMCFVCluwL4+tP+pNyf79B2dwZdmtO90hOEODR7ue9qOGrTT2zmbgWpY1VnPD/\n" +
            "DocbVvQtIp+Hz4+8lSHaDy2N9TThUdo=\n" +
            "-----END CERTIFICATE-----\n"

    private lateinit var skByteArray: ByteArray
    private lateinit var pkByteArray: ByteArray

    private val message = "This is test message for aggregation gamma signature"

    @Before
    fun setUp() {
        Security.insertProviderAt(BouncyCastleProvider(), 1)
        skByteArray = testSk.toPrivateKey()!!.d.toByteArray().encodeHex()
        val cert = testCert.toX509Cert()!!.publicKey as BCECPublicKey
        pkByteArray = cert.q.getEncoded(false).encodeHex()
    }

    @Test
    fun sign() {
        val signature = SignatureHelper.sign(skByteArray, pkByteArray, message.toByteArray())

    }
}