package io.tethys.tethyswallet.data.tethys.queries

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 스탠다드컨트랙트를 받아오기 위한 쿼리
 *
 * @property type QueryType
 */
class GetContract(
    override val type: QueryType = QueryType.CONTRACT_GET,
    override val where: Request
) : QueryData() {
    data class Request(
        val cid: String // 컨트랙트 아이디(필수)
    ) : QueryData.Request

    data class Result(
        @JsonProperty("contract") val contract: String
    ) : QueryData.Result
}