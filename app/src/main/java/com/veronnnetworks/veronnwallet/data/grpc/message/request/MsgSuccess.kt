package com.veronnnetworks.veronnwallet.data.grpc.message.request

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.veronnnetworks.veronnwallet.data.grpc.message.TypeMac
import com.veronnnetworks.veronnwallet.data.grpc.message.TypeMsg
import com.veronnnetworks.veronnwallet.utils.VeronnConfigs

data class MsgSuccess constructor(
    @Expose
    @SerializedName("time") // __TIMESTAMP__
    val time: Int,
    @Expose
    @SerializedName("user") // __BASE58_256__
    val user: String,
    @Expose
    @SerializedName("val") // __BOOLEAN__
    val result: Boolean
) : MsgPacker() {
    init {
        setHeader()
    }

    override fun setHeader() {
        this.header.msgType = TypeMsg.MSG_SUCCESS
        this.header.macType = TypeMac.HMAC
        this.header.sender = user
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