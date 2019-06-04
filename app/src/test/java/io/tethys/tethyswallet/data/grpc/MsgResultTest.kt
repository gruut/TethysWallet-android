package io.tethys.tethyswallet.data.grpc

import io.mockk.mockkStatic
import io.tethys.tethyswallet.data.grpc.message.MsgHeader
import io.tethys.tethyswallet.data.grpc.message.TypeMsg
import io.tethys.tethyswallet.data.grpc.message.TypeSerialization
import io.tethys.tethyswallet.data.grpc.message.response.MsgResult
import io.tethys.tethyswallet.data.grpc.message.response.MsgUnpacker
import io.tethys.tethyswallet.data.tethys.queries.GetUserCert
import io.tethys.tethyswallet.ui.BaseApp
import io.tethys.tethyswallet.utils.TethysConfigs
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MsgResultTest {

    private val header: MsgHeader = MsgHeader()
    private val testString = "{\n" +
            "  \"name\": [\n" +
            "    \"sn\", \"before\", \"after\", \"cert\"\n" +
            "  ],\n" +
            "  \"data\": [\n" +
            "    [\n" +
            "      \"123\",\n" +
            "      \"2019-06-04T05:03:19+0000\",\n" +
            "      \"2020-06-04T05:03:19+0000\",\n" +
            "      \"--- CERTIFICATE ---\\n...\"\n" +
            "    ],\n" +
            "    [\n" +
            "      \"124\",\n" +
            "      \"2019-06-04T05:03:19+0000\",\n" +
            "      \"2020-06-04T05:03:19+0000\",\n" +
            "      \"--- CERTIFICATE ---\\n...\"\n" +
            "    ]\n" +
            "  ]\n" +
            "}"

    private val testEmptyString = "{\n" +
            "  \"name\": [\"sn\", \"before\", \"after\", \"cert\"],\n" +
            "  \"data\": [[]]\n" +
            "}"

    @Before
    fun setup() {
        header.msgType = TypeMsg.MSG_RESULT
        header.serializationType = TypeSerialization.NONE

        mockkStatic(BaseApp::class)
    }

    @Test
    fun jsonDeserializeTest() {
        header.totalLength = TethysConfigs.HEADER_LENGTH + testString.length

        val target =
            MsgUnpacker(header.toByteArray() + testString.toByteArray()).body as MsgResult
        val list = GetUserCert.Result.getResultList(target.jsonStr)
        assertEquals(list.size, 2)
    }

    @Test
    fun emptyDeserializeTest() {
        header.totalLength = TethysConfigs.HEADER_LENGTH + testEmptyString.length

        val target =
            MsgUnpacker(header.toByteArray() + testEmptyString.toByteArray()).body as MsgResult
        val list = GetUserCert.Result.getResultList(target.jsonStr)
        assertEquals(list.size, 0)
    }
}