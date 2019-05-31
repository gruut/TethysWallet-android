package io.tethys.tethyswallet.data.grpc.message.response

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.tethys.tethyswallet.utils.ext.decodeBase58
import io.tethys.tethyswallet.utils.ext.longBytes
import io.tethys.tethyswallet.utils.ext.toSha256
import java.util.*

data class MsgReqSsig(
    @JsonProperty("block")
    val block: Block,
    @JsonProperty("producer")
    val producer: Producer,
    @JsonIgnore
    override val merger: String = producer.id
) : MsgBody {
    data class Block(
        @JsonProperty("id") // __BASE58_256__
        val id: String,
        @JsonProperty("time") // __TIMESTAMP__
        val time: Int,
        @JsonProperty("world") // __ALPHA_8__
        val world: String,
        @JsonProperty("chain") // __ALPHA_8__
        val chain: String,
        @JsonProperty("height") // __DECIMAL__
        val height: Int,
        @JsonProperty("previd") // __BASE58_256__
        val previd: String,
        @JsonProperty("txroot") // __BASE64_256__
        val txroot: String,
        @JsonProperty("usroot") // __BASE64_256__
        val usroot: String,
        @JsonProperty("csroot") // __BASE64_256__
        val csroot: String
    )

    data class Producer(
        @JsonProperty("id") // __BASE58_256__
        val id: String,
        @JsonProperty("sig") // __BASE64_SIG__
        val sig: String
    )

    fun validateId(): Boolean =
        Arrays.equals(
            block.id.decodeBase58(),
            (producer.id.decodeBase58() + block.time.longBytes() +
                    block.world.toByteArray(Charsets.UTF_8) + block.chain.toByteArray(Charsets.UTF_8) +
                    block.height.longBytes() + block.previd.decodeBase58()
                    ).toSha256()
        )
}