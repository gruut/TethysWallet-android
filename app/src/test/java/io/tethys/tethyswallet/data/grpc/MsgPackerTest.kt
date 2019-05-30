package io.tethys.tethyswallet.data.grpc

import com.fasterxml.jackson.databind.ObjectMapper
import io.tethys.tethyswallet.data.grpc.message.TypeMode
import io.tethys.tethyswallet.data.grpc.message.request.MsgJoin
import io.tethys.tethyswallet.data.grpc.message.request.MsgSuccess
import io.tethys.tethyswallet.utils.ext.encodeToBase58String
import io.tethys.tethyswallet.utils.ext.getTimestamp
import io.tethys.tethyswallet.utils.ext.toSha256
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class MsgPackerTest {
    @Test
    fun parse() {
        val user = "user".toSha256()
        val merger = "merger".toSha256()
        val time = getTimestamp()
        val world = "TETHYS"
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

    @Test
    fun enumParseTest() {

        val user = "user".toSha256()
        val msg = MsgSuccess(
            getTimestamp(),
            user.encodeToBase58String(),
            TypeMode.SIGNER.mode,
            true
        ).apply {
            this.sharedSecretKey = "ssk".toByteArray(Charsets.UTF_8)
        }

        val json = msg.jsonToByteArray()


        with(ObjectMapper()) {
            val obj = readValue(json, MsgSuccess::class.java)

            assertThat(obj.user, equalTo(user.encodeToBase58String()))
            assertThat(obj.mode, equalTo(TypeMode.SIGNER.mode))
            assertThat(obj.result, equalTo(true))
        }
    }
}