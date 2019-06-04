package io.tethys.tethyswallet.data.tethys.queries

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

/**
 * 사용자 변수를 받아오기 위한 쿼리
 *
 * @property type QueryType
 */
class GetUserScope(
    override val type: QueryType = QueryType.USER_SCOPE_GET,
    override val where: Request
) : QueryData() {
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    data class Request(
        val uid: String,            // 사용자 아이디(필수) (= var_owner)
        val pid: String? = null,    // 변수 아이디
        val type: String? = null,   // 변수 타입
        val name: String? = null,   // var_name(notag=true일 경우 필수)
        @JsonProperty("notag")
        val noTag: String? = null   // true/false
    ) : QueryData.Request

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    data class Result(
        @JsonProperty("var_name") val varName: String,
        @JsonProperty("var_value") val varValue: String,
        @JsonProperty("var_type") val varType: String,
        @JsonProperty("up_time") val upTime: String,
        @JsonProperty("up_block") val upBlock: String,
        @JsonProperty("tag") val tag: String,
        @JsonProperty("pid") val pid: String
    ) : QueryData.Result {
        companion object {
            fun getResultList(str: String): List<Result> = jacksonObjectMapper().readValue(str)
        }
    }
}