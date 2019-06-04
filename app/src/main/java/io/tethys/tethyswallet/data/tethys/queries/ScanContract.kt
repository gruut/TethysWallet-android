package io.tethys.tethyswallet.data.tethys.queries

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

/**
 * 스탠다드컨트랙트를 검색하기 위한 쿼리
 *
 * @property type QueryType
 */
class ScanContract(
    override val type: QueryType = QueryType.CONTRACT_SCAN,
    override val where: Request
) : QueryData() {
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    data class Request(
        val desc: String? = null,   // 컨트랙트를 검색할 키워드
        val author: String? = null, // 컨트랙트 저자
        val after: String? = null,  // 컨트랙트 유효 기간 시작
        val before: String? = null  // 컨트랙트 유효 기간 끝
    ) : QueryData.Request

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    data class Result(
        @JsonProperty("cid") val cid: String
    ) : QueryData.Result {
        companion object {
            fun getResultList(str: String): List<Result> = jacksonObjectMapper().readValue(str)
        }
    }
}