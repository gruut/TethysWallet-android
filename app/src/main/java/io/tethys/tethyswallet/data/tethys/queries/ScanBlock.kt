package io.tethys.tethyswallet.data.tethys.queries

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

/**
 * 블록을 검색하기 위한 쿼리
 *
 * @property type QueryType
 */
class ScanBlock(
    override val type: QueryType = QueryType.BLOCK_SCAN,
    override val where: Request
) : QueryData() {
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    data class Request(
        @JsonProperty("ss_id")
        val ssId: String? = null,       // 서명자 아이디
        val producer: String? = null,   // 블록 생성자 아이디
        val endorser: String? = null    // 보증자 아이디
    ) : QueryData.Request

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    data class Result(
        @JsonProperty("block_id") val blockId: String
    ) : QueryData.Result {
        companion object {
            fun getResultList(str: String): List<Result> = jacksonObjectMapper().readValue(str)
        }
    }
}