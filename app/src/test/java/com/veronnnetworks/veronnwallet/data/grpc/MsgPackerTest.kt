package com.veronnnetworks.veronnwallet.data.grpc

import com.fasterxml.jackson.databind.ObjectMapper
import com.veronnnetworks.veronnwallet.data.grpc.message.request.MsgJoin
import com.veronnnetworks.veronnwallet.utils.ext.encodeToBase58String
import com.veronnnetworks.veronnwallet.utils.ext.getTimestamp
import com.veronnnetworks.veronnwallet.utils.ext.toSha256
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class MsgPackerTest {
    @Test
    fun parse() {
        val user = "user".toSha256()
        val merger = "merger".toSha256()
        val time = getTimestamp()
        val world = "VERONN"
        val chain = "TSTCHAIN"
        val msg =
            MsgJoin(time, world, chain, user.encodeToBase58String(), merger.encodeToBase58String())

        val json = msg.jsonToByteArray()

        with(ObjectMapper()) {
            val obj = readValue(json, MsgJoin::class.java)

            assertThat(obj.time, equalTo(time))
            assertThat(obj.world, equalTo(world))
            assertThat(obj.chain, equalTo(chain))
            assertThat(obj.user, equalTo(user.encodeToBase58String()))
            assertThat(obj.merger, equalTo(merger.encodeToBase58String()))
        }
    }
}