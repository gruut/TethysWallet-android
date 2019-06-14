package io.tethys.tethyswallet.data.grpc.message.request

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import io.tethys.tethyswallet.data.grpc.message.TypeMsg
import io.tethys.tethyswallet.data.local.PreferenceHelper
import io.tethys.tethyswallet.ui.BaseApp
import io.tethys.tethyswallet.utils.TethysConfigs

data class MsgSetupMerger constructor(
    @get:JsonProperty("enc_sk") // PKCS8 PEM
    val encryptedSecretKey: String,
    @get:JsonProperty("cert") // x509 PEM
    val certificate: String
) : MsgPacker() {
    @JsonIgnore
    override var sharedSecretKey: ByteArray? = null

    private val prefHelper: PreferenceHelper = BaseApp.prefHelper

    init {
        setHeader()
    }

    override fun setHeader() {
        this.header.msgType = TypeMsg.MSG_SETUP_MERGER
        this.header.totalLength = TethysConfigs.HEADER_LENGTH + serialize().size
        this.header.worldId = prefHelper.worldId
        this.header.chainId = prefHelper.chainId
    }
}