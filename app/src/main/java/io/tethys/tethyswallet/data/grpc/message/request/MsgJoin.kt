package io.tethys.tethyswallet.data.grpc.message.request

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import io.tethys.tethyswallet.data.grpc.message.TypeMsg
import io.tethys.tethyswallet.data.local.PreferenceHelper
import io.tethys.tethyswallet.ui.BaseApp
import io.tethys.tethyswallet.utils.TethysConfigs

data class MsgJoin constructor(
    @get:JsonProperty("time") // __TIMESTAMP__
    val time: Int,
    @get:JsonProperty("world") // __ALPHA_8__
    val world: String,
    @get:JsonProperty("chain") // __ALPHA_8__
    val chain: String,
    @get:JsonProperty("user") // __BASE58_256__
    val user: String,
    @get:JsonProperty("merger") // __BASE58_256__
    val merger: String
) : MsgPacker() {
    @JsonIgnore
    override var sharedSecretKey: ByteArray? = null

    private val prefHelper: PreferenceHelper = BaseApp.prefHelper

    init {
        setHeader()
        prefHelper.worldId = world
        prefHelper.chainId = chain
    }

    override fun setHeader() {
        this.header.msgType = TypeMsg.MSG_JOIN
        this.header.worldId = world
        this.header.chainId = chain
        this.header.sender = user
        this.header.totalLength = TethysConfigs.HEADER_LENGTH + serialize().size
    }

    override fun toString(): String {
        return header.toString() + "\n" + String(serialize())
    }
}