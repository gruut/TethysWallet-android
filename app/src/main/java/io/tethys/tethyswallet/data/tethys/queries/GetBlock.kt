package io.tethys.tethyswallet.data.tethys.queries

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

/**
 * 블록을 얻어오기 위한 쿼리
 *
 * @property type QueryType
 */
class GetBlock(
    override val type: QueryType = QueryType.BLOCK_GET,
    override val where: Request
) : QueryData() {
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    data class Request(
        @JsonProperty("block_id")
        val blockId: String? = null,        // 블록의 아이디
        @JsonProperty("block_height")
        val blockHeight: String? = null,    // 블록의 높이
        @JsonProperty("txid")
        val txId: String? = null            // 포함된 TX 아이디
    ) : QueryData.Request

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    data class Result(
        @JsonProperty("block") val block: String
    ) : QueryData.Result {
        companion object {
            fun getResultList(str: String): List<Result> = jacksonObjectMapper().readValue(str)
        }
    }
}