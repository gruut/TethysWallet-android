package io.tethys.tethyswallet.data.tethys.contracts

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.StdSerializer

@JsonSerialize(using = UpdateUserCert.CustomSerializer::class)
data class UpdateUserCert(
    val cert: String,        // __PEM__          x.509 certificate
    override val contractType: ContractType = ContractType.UPDATE_USER_CERTIFICATE
) : StandardContractInput() {
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
            gen.writeObjectField("cert", value.cert)
            gen.writeEndObject()
            gen.writeEndArray()
        }

    }
}