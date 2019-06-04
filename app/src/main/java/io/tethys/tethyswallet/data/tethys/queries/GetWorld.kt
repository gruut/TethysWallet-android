package io.tethys.tethyswallet.data.tethys.queries

import com.fasterxml.jackson.annotation.JsonProperty

class GetWorld(
    override val type: QueryType = QueryType.WORLD_GET,
    override val where: Request? = null
) : QueryData() {
    data class Result(
        @JsonProperty("world_id") val worldId: String,
        @JsonProperty("created_time") val createdTime: String,
        @JsonProperty("creator_id") val creatorId: String,
        @JsonProperty("creator_pk") val creatorPk: String,
        @JsonProperty("authority_id") val authorityId: String,
        @JsonProperty("authority_pk") val authorityPk: String,
        @JsonProperty("keyc_name") val keycName: String,
        @JsonProperty("keyc_initial_amount") val keycInitialAmount: String,
        @JsonProperty("allow_mining") val allowMining: String,
        @JsonProperty("mining_rule") val miningRule: String,
        @JsonProperty("allow_anonymous_user") val allowAnonymousUser: String,
        @JsonProperty("join_fee") val joinFee: String
    ) : QueryData.Result
}