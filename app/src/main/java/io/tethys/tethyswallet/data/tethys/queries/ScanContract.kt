package io.tethys.tethyswallet.data.tethys.queries

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 스탠다드컨트랙트를 검색하기 위한 쿼리
 *
 * @property type QueryType
 */
class ScanContract(
    override val type: QueryType = QueryType.CONTRACT_SCAN,
    override val where: Request
) : QueryData() {
    data class Request(
        val desc: String? = null,   // 컨트랙트를 검색할 키워드
        val author: String? = null, // 컨트랙트 저자
        val after: String? = null,  // 컨트랙트 유효 기간 시작
        val before: String? = null  // 컨트랙트 유효 기간 끝
    ) : QueryData.Request

    data class Result(
        @JsonProperty("cid") val cid: String
    ) : QueryData.Result
}