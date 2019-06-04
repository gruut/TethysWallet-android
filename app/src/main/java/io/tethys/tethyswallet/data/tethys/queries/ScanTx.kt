package io.tethys.tethyswallet.data.tethys.queries

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * TX를 검색하기 위한 쿼리
 *
 * @property type QueryType
 */
class ScanTx(
    override val type: QueryType = QueryType.TX_SCAN,
    override val where: Request
) : QueryData() {
    data class Request(
        @JsonProperty("tx_user")
        val txUser: String? = null,        // TX 생성자 아이디
        @JsonProperty("tx_receiver")
        val txReceiver: String? = null,    // TX 수신자 아이디
        @JsonProperty("tx_author")
        val txAuthor: String? = null,      // 저자 아이디
        val cid: String? = null            // 컨트랙트 아이디
    ) : QueryData.Request

    data class Result(
        @JsonProperty("txid") val txId: String
    ) : QueryData.Result
}