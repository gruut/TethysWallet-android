package io.tethys.tethyswallet.data.grpc.message.request

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import io.tethys.tethyswallet.data.grpc.message.TypeMac
import io.tethys.tethyswallet.data.grpc.message.TypeMsg
import io.tethys.tethyswallet.data.local.PreferenceHelper
import io.tethys.tethyswallet.ui.BaseApp
import io.tethys.tethyswallet.utils.TethysConfigs

data class MsgSuccess constructor(
    @get:JsonProperty("time") // __TIMESTAMP__
    val time: Int,
    @get:JsonProperty("user") // __BASE58_256__
    val user: String,
    @get:JsonProperty("mode") // __ENUM_STRING__
    val mode: String,
    @get:JsonProperty("val") // __BOOLEAN__
    val result: Boolean
) : MsgPacker() {
    @JsonIgnore
    override var sharedSecretKey: ByteArray? = null

    private val prefHelper: PreferenceHelper = BaseApp.prefHelper

    init {
        setHeader()
    }

    override fun setHeader() {
        this.header.msgType = TypeMsg.MSG_SUCCESS
        this.header.macType = TypeMac.HMAC
        this.header.worldId = prefHelper.worldId
        this.header.chainId = prefHelper.chainId
        this.header.sender = user
        this.header.totalLength = TethysConfigs.HEADER_LENGTH + serialize().size
    }

    override fun toString(): String {
        return header.toString() + "\n" + String(serialize())
    }
}