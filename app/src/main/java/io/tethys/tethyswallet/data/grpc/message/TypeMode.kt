package io.tethys.tethyswallet.data.grpc.message

enum class TypeMode(val mode: String) {
    USER("user"),
    SIGNER("signer"),
    ALL("all");

    companion object {
        fun getByValue(value: String) = values().firstOrNull { it.mode == value }
    }
}