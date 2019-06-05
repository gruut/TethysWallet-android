package io.tethys.tethyswallet.data.grpc.message.request

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import io.tethys.tethyswallet.data.grpc.message.TypeMsg
import io.tethys.tethyswallet.data.local.PreferenceHelper
import io.tethys.tethyswallet.ui.BaseApp
import io.tethys.tethyswallet.utils.TethysConfigs

data class MsgResponse1 constructor(
    @get:JsonProperty("time") // __TIMESTAMP__
    val time: Int,
    @get:JsonProperty("un") // __BASE64_256__
    val un: String,
    @get:JsonProperty("dh")
    val dh: DHJson,
    @get:JsonProperty("user")
    val user: UserJson
) : MsgPacker() {
    data class DHJson constructor(
        @get:JsonProperty("x") // __HEX_256__
        val x: String,
        @get:JsonProperty("y") // __HEX_256__
        val y: String
    )

    data class UserJson constructor(
        @get:JsonProperty("id") // __BASE58_256__
        val id: String,
        @get:JsonProperty("pk") // __CERT_PEM__ / __PK__
        val pk: String,
        @get:JsonProperty("sig") // __BASE64_SIG__
        val sig: String
    )

    @JsonIgnore
    override var sharedSecretKey: ByteArray? = null

    private val prefHelper: PreferenceHelper = BaseApp.prefHelper

    init {
        setHeader()
    }

    override fun setHeader() {
        this.header.msgType = TypeMsg.MSG_RESPONSE_1
        this.header.worldId = prefHelper.worldId
        this.header.chainId = prefHelper.chainId
        this.header.sender = user.id
        this.header.totalLength = TethysConfigs.HEADER_LENGTH + serialize().size
    }

    override fun toString(): String {
        return header.toString() + "\n" + String(serialize())
    }
}