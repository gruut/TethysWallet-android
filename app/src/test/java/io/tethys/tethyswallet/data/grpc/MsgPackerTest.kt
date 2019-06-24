package io.tethys.tethyswallet.data.grpc

import io.mockk.mockkStatic
import io.tethys.tethyswallet.data.grpc.message.TypeMode
import io.tethys.tethyswallet.data.grpc.message.request.MsgJoin
import io.tethys.tethyswallet.data.grpc.message.request.MsgSuccess
import io.tethys.tethyswallet.ui.BaseApp
import io.tethys.tethyswallet.utils.ext.encodeToBase58String
import io.tethys.tethyswallet.utils.ext.getTimestamp
import io.tethys.tethyswallet.utils.ext.toSha256
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MsgPackerTest {
    @Before
    fun setup() {
        mockkStatic(BaseApp::class)
    }

    @Test
    fun parse() {
        val user = "user".toSha256()
        val merger = "merger".toSha256()
        val time = getTimestamp()
        val world = "TETHYS"
        val chain = "TSTCHAIN"
        val msg =
            MsgJoin(
                time.toString(),
                world,
                chain,
                user.encodeToBase58String(),
                merger.encodeToBase58String()
            )

        val json = msg.serialize()
    }

    @Test
    fun enumParseTest() {
        val user = "user".toSha256()
        val msg = MsgSuccess(
            getTimestamp().toString(),
            user.encodeToBase58String(),
            TypeMode.SIGNER.mode,
            true
        ).apply {
            this.sharedSecretKey = "ssk".toByteArray(Charsets.UTF_8)
        }

        val json = msg.serialize()
    }
}