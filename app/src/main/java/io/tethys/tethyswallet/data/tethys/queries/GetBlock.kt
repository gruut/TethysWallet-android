package io.tethys.tethyswallet.data.tethys.queries

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 블록을 얻어오기 위한 쿼리
 *
 * @property type QueryType
 */
class GetBlock(
    override val type: QueryType = QueryType.BLOCK_GET,
    override val where: Request
) : QueryData() {
    data class Request(
        @JsonProperty("block_id")
        val blockId: String? = null,        // 블록의 아이디
        @JsonProperty("block_height")
        val blockHeight: String? = null,    // 블록의 높이
        @JsonProperty("txid")
        val txId: String? = null            // 포함된 TX 아이디
    ) : QueryData.Request

    data class Result(
        @JsonProperty("block") val block: String
    ) : QueryData.Result
}