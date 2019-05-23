package com.veronnnetworks.veronnwallet.data.grpc.message.response

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MsgAccept constructor(
    bytes: ByteArray
) : MsgUnpacker(bytes) {
    @Expose
    @SerializedName("time") // __TIMESTAMP__
    val time: Int
    @Expose
    @SerializedName("merger") // __BASE58_256__
    val merger: String
    @Expose
    @SerializedName("val") // __BOOLEAN__
    val result: Boolean

    init {
        val msg = byteArrayToJson() as MsgAccept
        this.time = msg.time
        this.merger = msg.merger
        this.result = msg.result
    }

    override fun byteArrayToJson(): MsgUnpacker {
        // Super class는 제외하고 deserialize
        val gson = GsonBuilder().addDeserializationExclusionStrategy(object : ExclusionStrategy {
            override fun shouldSkipField(f: FieldAttributes): Boolean {
                return f.declaringClass == MsgUnpacker::class.java
            }

            override fun shouldSkipClass(clazz: Class<*>): Boolean {
                return false
            }
        }).create()

        return gson.fromJson<MsgAccept>(String(body), MsgAccept::class.java)
    }

    override fun checkSender(): Boolean = header.sender.equals(merger)
}