package io.tethys.tethyswallet.data.grpc.message.request

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import io.tethys.tethyswallet.data.grpc.message.TypeMac
import io.tethys.tethyswallet.data.grpc.message.TypeMsg
import io.tethys.tethyswallet.data.local.PreferenceHelper
import io.tethys.tethyswallet.data.tethys.queries.QueryData
import io.tethys.tethyswallet.ui.BaseApp
import io.tethys.tethyswallet.utils.TethysConfigs

@JsonSerialize(using = MsgQuery.CustomSerializer::class)
data class MsgQuery constructor(
    val queryData: QueryData
) : MsgPacker() {
    @JsonIgnore
    override var sharedSecretKey: ByteArray? = null

    private val prefHelper: PreferenceHelper = BaseApp.prefHelper

    init {
        setHeader()
    }

    override fun setHeader() {
        this.header.msgType = TypeMsg.MSG_QUERY
        this.header.macType = TypeMac.HMAC
        this.header.worldId = prefHelper.worldId
        this.header.chainId = prefHelper.chainId
        this.header.totalLength = TethysConfigs.HEADER_LENGTH + serialize().size
    }

    override fun toString(): String {
        return header.toString() + "\n" + String(serialize())
    }

    class CustomSerializer : StdSerializer<MsgQuery>(
        MsgQuery::class.java
    ) {
        override fun serialize(
            value: MsgQuery,
            gen: JsonGenerator,
            provider: SerializerProvider
        ) {
            gen.writeStartObject()
            gen.writeObjectField("type", value.queryData.type.value)
            gen.writeObjectField("where", value.queryData.where)
            gen.writeEndObject()
        }
    }
}