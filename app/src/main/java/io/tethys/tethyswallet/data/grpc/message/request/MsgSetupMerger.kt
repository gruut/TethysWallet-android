package io.tethys.tethyswallet.data.grpc.message.request

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import io.tethys.tethyswallet.data.grpc.message.TypeMsg
import io.tethys.tethyswallet.utils.TethysConfigs

data class MsgSetupMerger constructor(
    @JsonProperty("enc_sk") // PKCS8 PEM
    val encryptedSecretKey: String,
    @JsonProperty("cert") // x509 PEM
    val certificate: String
) : MsgPacker() {
    @JsonIgnore
    override var sharedSecretKey: ByteArray? = null

    init {
        setHeader()
    }

    override fun setHeader() {
        this.header.msgType = TypeMsg.MSG_SETUP_MERGER
        this.header.totalLength = TethysConfigs.HEADER_LENGTH + jsonToByteArray().serialize().size
    }

    override fun jsonToByteArray(): ByteArray {
        with(ObjectMapper()) {
            return writeValueAsBytes(this@MsgSetupMerger)
        }
    }

    override fun toString(): String {
        return header.toString() + "\n" + String(jsonToByteArray())
    }
}