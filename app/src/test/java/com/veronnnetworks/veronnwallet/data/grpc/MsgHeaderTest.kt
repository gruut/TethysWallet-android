package com.veronnnetworks.veronnwallet.data.grpc

import com.veronnnetworks.veronnwallet.data.grpc.message.MsgHeader
import com.veronnnetworks.veronnwallet.data.grpc.message.TypeMac
import com.veronnnetworks.veronnwallet.data.grpc.message.TypeMsg
import com.veronnnetworks.veronnwallet.data.grpc.message.TypeSerialization
import com.veronnnetworks.veronnwallet.utils.ext.encodeToBase58String
import com.veronnnetworks.veronnwallet.utils.ext.toSha256
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
        header.worldId = "VERONN"
        header.chainId = "TSTCHAIN"
        header.sender = sender.encodeToBase58String()

        val convertedHeader = MsgHeader(header.toByteArray())

        assertThat(convertedHeader.worldId, equalTo("VERONN"))
        assertThat(convertedHeader.chainId, equalTo("TSTCHAIN"))
        assertThat(convertedHeader.sender, equalTo(sender.encodeToBase58String()))
    }
}