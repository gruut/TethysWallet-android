package io.tethys.tethyswallet.data.tethys.queries

import com.fasterxml.jackson.annotation.JsonProperty

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
        val txId: String    // TX 아이디
    ) : QueryData.Request

    data class Result(
        @JsonProperty("tx") val tx: String
    ) : QueryData.Result
}