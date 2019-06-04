package io.tethys.tethyswallet.data.tethys.queries

abstract class QueryData {
    abstract val type: QueryType
    abstract val where: Request?

    interface Request
    interface Result

    enum class QueryType(val value: String) {
        WORLD_GET("world.get"),
        CHAIN_GET("chain.get"),
        CONTRACT_SCAN("contract.scan"),
        CONTRACT_GET("contract.get"),
        USER_CERT_GET("user.cert.get"),
        USER_INFO_GET("user.info.get"),
        USER_SCOPE_GET("user.scope.get"),
        CONTRACT_SCOPE_GET("contract.scope.get"),
        BLOCK_GET("block.get"),
        TX_GET("tx.get"),
        BLOCK_SCAN("block.scan"),
        TX_SCAN("tx.scan");

        companion object {
            fun getByValue(value: String) = values().firstOrNull { it.value == value }
        }
    }
}