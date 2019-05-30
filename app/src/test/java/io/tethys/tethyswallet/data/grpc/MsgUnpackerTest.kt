package io.tethys.tethyswallet.data.grpc

import android.util.Base64
import io.mockk.mockkStatic
import io.tethys.tethyswallet.data.grpc.message.response.MsgChallenge
import io.tethys.tethyswallet.data.grpc.message.response.MsgUnpacker
import io.tethys.tethyswallet.utils.ext.encodeToBase58String
import io.tethys.tethyswallet.utils.ext.toSha256
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.security.SecureRandom

@RunWith(RobolectricTestRunner::class)
class MsgUnpackerTest {

    @Before
    fun setup() {
        mockkStatic(Base64::class)
    }

    @Test
    fun parse() {
        val user = "C8VQ4PGJBRRPVgAqtfNH866HETfBo4jyS1t3pM3QNhv3"
        val merger = "merger".toSha256()
        val nonce = ByteArray(32)
        SecureRandom().nextBytes(nonce)

        val msg = TestMsgChallenge(
            (System.currentTimeMillis() / 1000).toInt(),
            user,
            merger.encodeToBase58String(),
            Base64.encodeToString(nonce, Base64.NO_WRAP)
        )

        val target = MsgUnpacker(msg.jsonToByteArray()).body as MsgChallenge
        assertEquals(target.user, user)
        assertEquals(target.merger, merger.encodeToBase58String())
        assertEquals(target.mergerNonce, Base64.encodeToString(nonce, Base64.NO_WRAP))
    }
}