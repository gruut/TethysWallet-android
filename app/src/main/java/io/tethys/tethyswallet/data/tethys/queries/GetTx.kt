package io.tethys.tethyswallet.data.tethys.queries

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

/**
 * TX를 얻어오기 위한 쿼리
 *
 * @property type QueryType
 */
class GetTx(
    override val type: QueryType = QueryType.TX_GET,
    override val where: Request
) : QueryData() {
    data class Request(
        @JsonProperty("txid")
        val txId: String    // TX 아이디(필수)
    ) : QueryData.Request

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    data class Result(
        @JsonProperty("tx") val tx: String
    ) : QueryData.Result {
        companion object {
            fun getResultList(str: String): List<Result> = jacksonObjectMapper().readValue(str)
        }
    }
}