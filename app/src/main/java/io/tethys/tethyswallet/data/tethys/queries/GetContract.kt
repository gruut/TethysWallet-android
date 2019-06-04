package io.tethys.tethyswallet.data.tethys.queries

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

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

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    data class Result(
        @JsonProperty("contract") val contract: String
    ) : QueryData.Result {
        companion object {
            fun getResultList(str: String): List<Result> = jacksonObjectMapper().readValue(str)
        }
    }
}