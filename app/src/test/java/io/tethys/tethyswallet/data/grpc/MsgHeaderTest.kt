package io.tethys.tethyswallet.data.grpc

import io.tethys.tethyswallet.data.grpc.message.MsgHeader
import io.tethys.tethyswallet.data.grpc.message.TypeMac
import io.tethys.tethyswallet.data.grpc.message.TypeMsg
import io.tethys.tethyswallet.data.grpc.message.TypeSerialization
import io.tethys.tethyswallet.utils.ext.encodeToBase58String
import io.tethys.tethyswallet.utils.ext.toSha256
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test

class MsgHeaderTest {
    @Test
    fun convertHeaderTest() {
        val sender = "test".toSha256()

        val header: MsgHeader = MsgHeader()
        header.msgType = TypeMsg.MSG_JOIN
        header.macType = TypeMac.SHA256
        header.serializationType = TypeSerialization.CBOR
        header.totalLength = 87
        header.worldId = "TETHYS"
        header.chainId = "TSTCHAIN"
        header.sender = sender.encodeToBase58String()

        val convertedHeader = MsgHeader(header.toByteArray())

        assertThat(convertedHeader.worldId, equalTo("TETHYS"))
        assertThat(convertedHeader.chainId, equalTo("TSTCHAIN"))
        assertThat(convertedHeader.sender, equalTo(sender.encodeToBase58String()))
    }
}