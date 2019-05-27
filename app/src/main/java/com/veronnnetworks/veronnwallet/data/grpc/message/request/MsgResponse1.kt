package com.veronnnetworks.veronnwallet.data.grpc.message.request

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.veronnnetworks.veronnwallet.data.grpc.message.TypeMsg
import com.veronnnetworks.veronnwallet.utils.VeronnConfigs

data class MsgResponse1 constructor(
    @Expose
    @SerializedName("time") // __TIMESTAMP__
    val time: Int,
    @Expose
    @SerializedName("un") // __BASE64_256__
    val userNonce: String,
    @Expose
    @SerializedName("dh")
    val dh: DHJson,
    @Expose
    @SerializedName("user")
    val user: UserJson
) : MsgPacker() {
    data class DHJson constructor(
        @Expose
        @SerializedName("x") // __HEX_256__
        val x: String,
        @Expose
        @SerializedName("y") // __HEX_256__
        val y: String
    )

    data class UserJson constructor(
        @Expose
        @SerializedName("id") // __BASE58_256__
        val id: String,
        @Expose
        @SerializedName("pk") // __CERT_PEM__ / __PK__
        val pk: String,
        @Expose
        @SerializedName("sig") // __BASE64_SIG__
        val sig: String
    )

    init {
        setHeader()
    }

    override fun setHeader() {
        this.header.msgType = TypeMsg.MSG_RESPONSE_1
        this.header.sender = user.id
        this.header.totalLength = VeronnConfigs.HEADER_LENGTH + jsonToByteArray().serialize().size
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
        return gson.toJson(this).toByteArray()
    }
}