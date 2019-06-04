package io.tethys.tethyswallet.data.grpc

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.mockkStatic
import io.tethys.tethyswallet.data.grpc.message.request.MsgQuery
import io.tethys.tethyswallet.data.tethys.queries.ScanContract
import io.tethys.tethyswallet.ui.BaseApp
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual.equalTo
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MsgQueryTest {

    private val expectedStr =
        "{\"type\":\"contract.scan\",\"where\":{\"desc\":\"description\",\"author\":\"작성자\"}}"

    @Before
    fun setup() {
        mockkStatic(BaseApp::class)
    }

    @Test
    fun jsonSerializeTest() {
        val input = MsgQuery(
            ScanContract(where = ScanContract.Request("description", "작성자"))
        )

        val str = jacksonObjectMapper().writeValueAsString(input)
        assertThat(str, equalTo(expectedStr))
    }
}