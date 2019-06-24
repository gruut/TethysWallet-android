package io.tethys.tethyswallet.auth

import io.mockk.impl.annotations.InjectMockKs
import io.mockk.mockkStatic
import io.tethys.tethyswallet.ui.BaseApp
import io.tethys.tethyswallet.utils.rx.TestSchedulerProvider
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AppKeyStoreHelperTest {

    private val cert = "-----BEGIN CERTIFICATE-----\n" +
            "MIICjTCB9qADAgECAgcA7mfd6CwlMA0GCSqGSIb3DQEBCwUAMCYxCzAJBgNVBAYT\n" +
            "AktSMRcwFQYDVQQKDA5HcnV1dCBOZXR3b3JrczAeFw0xOTA2MTIwNzM2MjhaFw0y\n" +
            "OTA2MTIwNzM2MjhaMDcxNTAzBgNVBAMMLDRCRUJucGlWaEtSa0YzejZzd1VGMjV4\n" +
            "QlhQRDdhcHg1ZVJKN3ZqckhuRlZRMFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEX4Ys\n" +
            "qGtJZAh5dB1gR+FGk5A1Vk7h4A3qI39J3RbEnEWoJr/ggMGTUvebSVTBDbzqzYSq\n" +
            "2qhs1oFZ+qEYTFyazzANBgkqhkiG9w0BAQsFAAOCAYEAB8Lm+oZaAjRCQm1ioarL\n" +
            "nASCyUMAJPudOMls1wZR23kboUsAr3FZtzejzuEDVGdWmGpU01fVaTQa1CFP0H6H\n" +
            "WNHdke3mErPxO7/gcaXxyObc4TBHVnj8+EzJaq3BybRg+aV2+Py7c5pMtOEpCJZt\n" +
            "fRI2bCzDxAX+uh+UyBy0lXhgHvqKQfep0BoRsyfGCJKQ9v1nqY+JI0uy9wvMATw2\n" +
            "LN0AvExuPcnIr6+XyeH4Z/13bMpy43RPuTtdK0KUNcA2AGduhDyqC0P/cT3xJhyB\n" +
            "+46m99HMA3YUrqTHTv8xfInEncUu+SP04VusdKAF3b9vIcDshcpI3RMy/wKdA3Fq\n" +
            "gB4XQy4wwl0s9G1vLoi/eRmwvSZpy0bOGUxI6KLpv4G+fRAEfijF9GDIFhDWW35B\n" +
            "XBBChzNBNxNJ/P1lO25EFYQLT6QunsnpLJCOsiBDb2C1VGKwwMiWdotspsV4Vyhv\n" +
            "Tj1ie+wBdYqPo5g5g2ORlxz+ZyfKPXeu0w/FIzFqBmtM\n" +
            "-----END CERTIFICATE-----\n"

    private val pubKey =
        "045f862ca86b49640879741d6047e146939035564ee1e00dea237f49dd16c49c45a826bfe080c19352f79b4954c10dbceacd84aadaa86cd68159faa1184c5c9acf"
    private val prvKey = "26fa3fc309ef88a94abdeecccdace07d180cee8555f65f4b1b2830575397af5c"

    @InjectMockKs
    var keyStoreHelper = AppKeyStoreHelper(TestSchedulerProvider())

    @Before
    fun setup() {
        mockkStatic(BaseApp::class)
        BaseApp.prefHelper.worldId = "_TETHYS_"
        BaseApp.prefHelper.chainId = "TSTCHAIN"
        // BaseApp.prefHelper.ecPublicKey = pubKey.toByteArray()
        // BaseApp.prefHelper.ecSecretKey = prvKey.toByteArray()
    }

    @Test
    fun signAndVerify() {
        // keyStoreHelper.signWithECKey("testdata".toByteArray()).subscribe { t: String ->
        //     assertEquals(keyStoreHelper.verifyWithCert("testdata".toByteArray(), t, cert), true)
        // }
    }
}