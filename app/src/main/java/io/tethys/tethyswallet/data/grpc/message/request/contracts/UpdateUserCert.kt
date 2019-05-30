package io.tethys.tethyswallet.data.grpc.message.request.contracts

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.StdSerializer

@JsonSerialize(using = UpdateUserCert.CustomSerializer::class)
data class UpdateUserCert(
    val cert: String        // __PEM__          x.509 certificate
) : StandardContractInput {
    class CustomSerializer : StdSerializer<UpdateUserCert>(
        UpdateUserCert::class.java
    ) {
        override fun serialize(
            value: UpdateUserCert,
            gen: JsonGenerator,
            provider: SerializerProvider
        ) {
            gen.writeStartArray()
            gen.writeStartObject()
            gen.writeObjectField("time", value.cert)
            gen.writeEndObject()
            gen.writeEndArray()
        }

    }
}