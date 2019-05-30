package io.tethys.tethyswallet.data.grpc

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.tethys.tethyswallet.data.grpc.message.TypeMsg
import io.tethys.tethyswallet.data.grpc.message.request.MsgPacker
import io.tethys.tethyswallet.utils.TethysConfigs

data class TestMsgChallenge constructor(
    @Expose
    @SerializedName("time") // __TIMESTAMP__
    val time: Int,
    @Expose
    @SerializedName("user") // __BASE58_256__
    val user: String,
    @Expose
    @SerializedName("merger") // __BASE58_256__
    val merger: String,
    @Expose
    @SerializedName("mn") // __BASE64_256__
    val mergerNonce: String,
    override val sharedSecretKey: ByteArray? = null
) : MsgPacker() {
    init {
        setHeader()
    }

    override fun setHeader() {
        this.header.msgType = TypeMsg.MSG_CHALLENGE
        this.header.totalLength = TethysConfigs.HEADER_LENGTH + jsonToByteArray().serialize().size
    }

    override fun jsonToByteArray(): ByteArray {
        // Superclass 제외하고 serialize
        val gson = GsonBuilder().addSerializationExclusionStrategy(object : ExclusionStrategy {
            override fun shouldSkipField(f: FieldAttributes): Boolean {
                return f.declaringClass == MsgPacker::class.java
            }

            override fun shouldSkipClass(clazz: Class<*>): Boolean {
                return false
            }
        }).create()
        val tmp = gson.toJson(this)
        return tmp.toByteArray()
    }

}