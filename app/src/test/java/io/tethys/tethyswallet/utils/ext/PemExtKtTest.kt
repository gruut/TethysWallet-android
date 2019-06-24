package io.tethys.tethyswallet.utils.ext

import io.mockk.mockkStatic
import io.tethys.tethyswallet.ui.BaseApp
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PemExtKtTest {

    private val cert =
        "-----BEGIN CERTIFICATE-----\n\r\n\t\t\t\tMIICjTCB9qADAgECAgcQaE1b8dyrMA0GCSqGSIb3DQEBCwUAMCYxCzAJBgNVBAYT\n\r\n\t\t\t\tAktSMRcwFQYDVQQKDA5HcnV1dCBOZXR3b3JrczAeFw0xOTA2MTIwNjM1MjRaFw0y\n\r\n\t\t\t\tOTA2MTIwNjM1MjRaMDcxNTAzBgNVBAMMLDV3cWVyQUxHZkRDWHp6aFRTWnNRZXhI\n\r\n\t\t\t\tWmZiSE42SjYzdDJad3Z1SGZFOGt2MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEjYw+\n\r\n\t\t\t\tQvchMwHBIz3AayAYovTMelfXGZBvr0ud1sst4kPQHrbMqxxErXJI2Z9Fe3VqN86Z\n\r\n\t\t\t\tYkFdp4FeYriwwh92ZjANBgkqhkiG9w0BAQsFAAOCAYEAAWZf6QAqxAEaY8OyBK/3\n\r\n\t\t\t\tew/8AwHRxwF4lL/N0msX7fmcoWcMfhnMp5xGiTINdgeoR/1xiLu9UCX9q8k4FIpx\n\r\n\t\t\t\tJyu54luwhncw/PQgZwsUl3MfbgBvv6Nis5p4c3aw1qAp9lwhKJJCN+UJVdxRL/20\n\r\n\t\t\t\tqdciEvUnodt2mP4njBJN1vcYuAwI+T97x+6jDTChRLh20DH46n+AGD4RCehBCPrM\n\r\n\t\t\t\tfsXdJNS4DfBqordC92UyMFsu6B5u6S3RrTHFg4S7Rccv+6LGmh0ZSId+0zm5/O9E\n\r\n\t\t\t\t8ie3zeJzTShn+fgt3CC9V98Ij8myTx5rsKFw2HJYKoVdq5i4xNncORM8uc5mscvc\n\r\n\t\t\t\t9wCJ1CgtYC7Zf+QbIQBRD57XLvP+WoE4xM62zgzNa2IiwFxtq/jApugZEU2gfdgQ\n\r\n\t\t\t\tp7W7XZYlTTDjESzU/aEn0TSLqIHVcHRaeI3g/bZGrE8ZGGRr53uh3aFtjXhZ3WOa\n\r\n\t\t\t\tbaqQMm0JTzt2VWvZQLQHQDSqxiwzBTyuWT9Fet7OcFXP\n\r\n\t\t\t\t-----END CERTIFICATE-----\n"

    @Before
    fun setup() {
        mockkStatic(BaseApp::class)
    }

    @Test
    fun toX509Cert() {
        val x509Cert = cert.toX509Cert()
    }
}