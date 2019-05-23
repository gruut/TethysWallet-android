package com.veronnnetworks.veronnwallet.data.grpc.message.response

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class MsgChallenge constructor(
    bytes: ByteArray
) : MsgUnpacker(bytes) {

    @Expose
    @SerializedName("time") // __TIMESTAMP__
    val time: Int
    @Expose
    @SerializedName("user") // __BASE58_256__
    val user: String
    @Expose
    @SerializedName("merger") // __BASE58_256__
    val merger: String
    @Expose
    @SerializedName("mn") // __BASE64_256__
    val mergerNonce: String

    init {
        val msg = byteArrayToJson() as MsgChallenge
        this.time = msg.time
        this.user = msg.user
        this.merger = msg.merger
        this.mergerNonce = msg.mergerNonce
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

        return gson.fromJson<MsgChallenge>(String(body), MsgChallenge::class.java)
    }

    override fun checkSender(): Boolean = header.sender.equals(merger)
}