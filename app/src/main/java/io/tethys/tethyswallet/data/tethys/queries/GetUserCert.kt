package io.tethys.tethyswallet.data.tethys.queries

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

/**
 * 사용자의 인증서를 검색하기 위한 쿼리
 *
 * @property type QueryType
 */
class GetUserCert(
    override val type: QueryType = QueryType.USER_CERT_GET,
    override val where: Request
) : QueryData() {
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    data class Request(
        val uid: String,            // 사용자 아이디(필수)
        val before: String? = null, // 유효 기간 끝
        val after: String? = null,  // 유효 기간 시작
        val sn: String? = null      // 인증서 시리얼 번호
    ) : QueryData.Request

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    data class Result(
        @JsonProperty("sn") val sn: String,
        @JsonProperty("before") val before: String,
        @JsonProperty("after") val after: String,
        @JsonProperty("cert") val cert: String
    ) : QueryData.Result {
        companion object {
            fun getResultList(str: String): List<Result> = jacksonObjectMapper().readValue(str)
        }
    }
}