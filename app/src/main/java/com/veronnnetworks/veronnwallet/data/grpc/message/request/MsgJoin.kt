package com.veronnnetworks.veronnwallet.data.grpc.message.request

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.veronnnetworks.veronnwallet.data.grpc.message.TypeMsg
import com.veronnnetworks.veronnwallet.utils.VeronnConfigs

data class MsgJoin constructor(
    @Expose
    @SerializedName("time")
    val time: Int,
    @Expose
    @SerializedName("world")
    val world: String,
    @Expose
    @SerializedName("chain")
    val chain: String,
    @Expose
    @SerializedName("user")
    val user: String,
    @Expose
    @SerializedName("merger")
    val merger: String
) : MsgPacker() {
    init {
        setHeader()
    }

    override fun setHeader() {
        this.header.msgType = TypeMsg.MSG_JOIN
        this.header.worldId = world
        this.header.chainId = chain
        this.header.sender = user
        this.header.totalLength = VeronnConfigs.HEADER_LENGTH + toJson().serialize().size
    }

    override fun toJson(): ByteArray {
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