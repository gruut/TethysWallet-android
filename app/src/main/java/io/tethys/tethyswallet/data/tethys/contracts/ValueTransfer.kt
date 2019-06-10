package io.tethys.tethyswallet.data.tethys.contracts

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.StdSerializer

@JsonSerialize(using = ValueTransfer.CustomSerializer::class)
data class ValueTransfer(
    val amount: String, // __PINT__     amount of v-type variable for transfer
    val unit: String,   // __TINYTEXT__ unit type of v-type variable for transfer
    val pid: String,    // __BASE64__   id of v-type variable for transfer
    val tag: String,    // __XML__      condition for use of this v-type value
    override val contractType: ContractType = ContractType.VALUE_TRANSFER
) : StandardContractInput() {
    class CustomSerializer : StdSerializer<ValueTransfer>(
        ValueTransfer::class.java
    ) {
        override fun serialize(
            value: ValueTransfer,
            gen: JsonGenerator,
            provider: SerializerProvider
        ) {
            gen.writeStartArray()
            gen.writeStartObject()
            gen.writeObjectField("amount", value.amount)
            gen.writeEndObject()
            gen.writeStartObject()
            gen.writeObjectField("unit", value.unit)
            gen.writeEndObject()
            gen.writeStartObject()
            gen.writeObjectField("pid", value.pid)
            gen.writeEndObject()
            gen.writeStartObject()
            gen.writeObjectField("tag", value.tag)
            gen.writeEndObject()
            gen.writeEndArray()
        }

    }
}