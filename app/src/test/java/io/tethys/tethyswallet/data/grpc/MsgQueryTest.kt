package io.tethys.tethyswallet.data.grpc

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.mockkStatic
import io.tethys.tethyswallet.data.grpc.message.request.MsgQuery
import io.tethys.tethyswallet.data.tethys.queries.ScanContract
import io.tethys.tethyswallet.ui.BaseApp
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import timber.log.Timber

@RunWith(RobolectricTestRunner::class)
class MsgQueryTest {

    @Before
    fun setup() {
        mockkStatic(BaseApp::class)
    }

    @Test
    fun JsonSerializeTest() {
        val input =
            MsgQuery(
                ScanContract(
                    where = ScanContract.Request("description", "작성자")
                )
            )

        Timber.d(ObjectMapper().writeValueAsString(input))
    }
}