package com.veronnnetworks.veronnwallet.data.grpc

import com.veronnnetworks.veronnwallet.data.grpc.message.request.MsgJoin
import com.veronnnetworks.veronnwallet.utils.ext.encodeToBase58String
import com.veronnnetworks.veronnwallet.utils.ext.toSha256
import org.junit.Test

class MsgJoinTest {
    @Test
    fun parse() {
        val user = "user".toSha256()
        val merger = "merger".toSha256()
        val msg = MsgJoin(
            (System.currentTimeMillis() / 1000).toInt(),
            "VERONN",
            "TSTCHAIN",
            user.encodeToBase58String(),
            merger.encodeToBase58String()
        )

        val json = msg.toJson()
        val string = String(json)
    }
}