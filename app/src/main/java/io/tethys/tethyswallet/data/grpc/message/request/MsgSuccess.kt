package io.tethys.tethyswallet.data.grpc.message.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import io.tethys.tethyswallet.data.grpc.message.TypeMac
import io.tethys.tethyswallet.data.grpc.message.TypeMsg
import io.tethys.tethyswallet.utils.TethysConfigs

data class MsgSuccess constructor(
    @JsonProperty("time") // __TIMESTAMP__
    val time: Int,
    @JsonProperty("user") // __BASE58_256__
    val user: String,
    @JsonProperty("val") // __BOOLEAN__
    val result: Boolean,
    override val sharedSecretKey: ByteArray?
) : MsgPacker() {
    init {
        setHeader()
    }

    override fun setHeader() {
        this.header.msgType = TypeMsg.MSG_SUCCESS
        this.header.macType = TypeMac.HMAC
        this.header.sender = user
        this.header.totalLength = TethysConfigs.HEADER_LENGTH + jsonToByteArray().serialize().size
    }

    override fun jsonToByteArray(): ByteArray {
        with(ObjectMapper()) {
            return writeValueAsBytes(this@MsgSuccess)
        }
    }

    override fun toString(): String {
        return header.toString() + "\n" + String(jsonToByteArray())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MsgSuccess

        if (sharedSecretKey != null) {
            if (other.sharedSecretKey == null) return false
            if (!sharedSecretKey.contentEquals(other.sharedSecretKey)) return false
        } else if (other.sharedSecretKey != null) return false

        return true
    }

    override fun hashCode(): Int {
        return sharedSecretKey?.contentHashCode() ?: 0
    }
}