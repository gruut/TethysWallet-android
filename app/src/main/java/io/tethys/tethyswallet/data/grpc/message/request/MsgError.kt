package io.tethys.tethyswallet.data.grpc.message.request

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import io.tethys.tethyswallet.Reply
import io.tethys.tethyswallet.data.grpc.message.TypeMac
import io.tethys.tethyswallet.data.grpc.message.TypeMsg
import io.tethys.tethyswallet.data.local.PreferenceHelper
import io.tethys.tethyswallet.ui.BaseApp
import io.tethys.tethyswallet.utils.TethysConfigs

data class MsgError constructor(
    @JsonProperty("sender") // __BASE58_256__
    val sender: String,
    @JsonProperty("time") // __TIMESTAMP__
    val time: Int,
    @JsonProperty("code") // __ERROR_CODE__
    val code: Reply.Status,
    @JsonProperty("info") // *
    val info: String? = null
) : MsgPacker() {
    @JsonIgnore
    override var sharedSecretKey: ByteArray? = null

    private val prefHelper: PreferenceHelper = BaseApp.prefHelper

    init {
        setHeader()
    }

    override fun setHeader() {
        this.header.msgType = TypeMsg.MSG_ERROR
        this.header.macType = TypeMac.NONE
        this.header.worldId = prefHelper.worldId
        this.header.chainId = prefHelper.chainId
        this.header.sender = sender
        this.header.totalLength = TethysConfigs.HEADER_LENGTH + jsonToByteArray().serialize().size
    }

    override fun jsonToByteArray(): ByteArray {
        with(ObjectMapper()) {
            return writeValueAsBytes(this@MsgError)
        }
    }

    override fun toString(): String {
        return header.toString() + "\n" + String(jsonToByteArray())
    }
}