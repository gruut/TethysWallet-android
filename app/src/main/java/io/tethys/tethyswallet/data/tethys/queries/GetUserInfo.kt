package io.tethys.tethyswallet.data.tethys.queries

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

/**
 * 사용자의 정보를 얻기 위한 쿼리
 *
 * @property type QueryType
 */
class GetUserInfo(
    override val type: QueryType = QueryType.USER_INFO_GET,
    override val where: Request
) : QueryData() {
    data class Request(
        val uid: String // 사용자 아이디(필수)
    ) : QueryData.Request

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    data class Result(
        @JsonProperty("register_day") val registerDay: String,
        @JsonProperty("register_code") val registerCode: String,
        @JsonProperty("gender") val gender: String,
        @JsonProperty("isc_type") val iscType: String,
        @JsonProperty("isc_code") val iscCode: String,
        @JsonProperty("location") val location: String,
        @JsonProperty("age_limit") val ageLimit: String
    ) : QueryData.Result {
        companion object {
            fun getResultList(str: String): List<Result> = jacksonObjectMapper().readValue(str)
        }
    }
}