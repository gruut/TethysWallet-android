package io.tethys.tethyswallet.data.tethys.queries

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 컨트랙트 변수를 받아오기 위한 쿼리
 *
 * @property type QueryType
 */
class GetContractScope(
    override val type: QueryType = QueryType.CONTRACT_SCOPE_GET,
    override val where: Request
) : QueryData() {
    data class Request(
        val cid: String,            // 컨트랙트 아이디(필수)
        val pid: String? = null,    // 변수 아이디
        val type: String? = null,   // 변수 타입
        val name: String? = null,   // var_name(notag=true일 경우 필수)
        @JsonProperty("notag")
        val noTag: String? = null   // true/false
    ) : QueryData.Request

    data class Result(
        @JsonProperty("var_name") val varName: String,
        @JsonProperty("var_value") val varValue: String,
        @JsonProperty("var_type") val varType: String,
        @JsonProperty("var_info") val varInfo: String,
        @JsonProperty("up_time") val upTime: String,
        @JsonProperty("up_block") val upBlock: String,
        @JsonProperty("pid") val pid: String
    ) : QueryData.Result
}