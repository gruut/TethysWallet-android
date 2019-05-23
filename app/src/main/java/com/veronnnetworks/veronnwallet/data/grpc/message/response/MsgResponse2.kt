package com.veronnnetworks.veronnwallet.data.grpc.message.response

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MsgResponse2 constructor(
    bytes: ByteArray
) : MsgUnpacker(bytes) {
    @Expose
    @SerializedName("time") // __TIMESTAMP__
    val time: Int
    @Expose
    @SerializedName("dh")
    val dh: DHJson
    @Expose
    @SerializedName("merger")
    val merger: MergerJson


    data class DHJson constructor(
        @Expose
        @SerializedName("x") // __HEX_256__
        val x: String,
        @Expose
        @SerializedName("y") // __HEX_256__
        val y: String
    )

    data class MergerJson constructor(
        @Expose
        @SerializedName("id") // __BASE58_256__
        val id: String,
        @Expose
        @SerializedName("cert") // __CERT_PEM__
        val cert: String,
        @Expose
        @SerializedName("sig") // __BASE64_SIG__
        val sig: String
    )

    init {
        val msg = byteArrayToJson() as MsgResponse2
        this.time = msg.time
        this.dh = msg.dh
        this.merger = msg.merger
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

        return gson.fromJson<MsgResponse2>(String(body), MsgResponse2::class.java)
    }

    override fun checkSender(): Boolean = header.sender.equals(merger.id)
}