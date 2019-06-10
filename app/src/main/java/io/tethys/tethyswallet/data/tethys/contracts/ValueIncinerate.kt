package io.tethys.tethyswallet.data.tethys.contracts

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.StdSerializer

@JsonSerialize(using = ValueIncinerate.CustomSerializer::class)
data class ValueIncinerate(
    val amount: String, // __PINT__     amount of v-type variable for incineration
    val pid: String,    // __BASE64__   id of v-type variable for incineration
    override val contractType: ContractType = ContractType.VALUE_INCINERATE
) : StandardContractInput() {
    class CustomSerializer : StdSerializer<ValueIncinerate>(
        ValueIncinerate::class.java
    ) {
        override fun serialize(
            value: ValueIncinerate,
            gen: JsonGenerator,
            provider: SerializerProvider
        ) {
            gen.writeStartArray()
            gen.writeStartObject()
            gen.writeObjectField("amount", value.amount)
            gen.writeEndObject()
            gen.writeStartObject()
            gen.writeObjectField("pid", value.pid)
            gen.writeEndObject()
            gen.writeEndArray()
        }

    }
}