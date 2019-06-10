package io.tethys.tethyswallet.data.tethys.contracts

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.StdSerializer

@JsonSerialize(using = ValueCreate.CustomSerializer::class)
data class ValueCreate(
    val amount: String,     // __PINT__     amount of v-type variable for creation
    val unit: String,       // __TINYTEXT__ unit name of v-type variable for creation
    val type: String,       // __ENUMV__    uint type of v-type variable for creation
    val condition: String,  // __XML__      condition for use of this v-type value
    override val contractType: ContractType = ContractType.VALUE_CREATE
) : StandardContractInput() {
    class CustomSerializer : StdSerializer<ValueCreate>(
        ValueCreate::class.java
    ) {
        override fun serialize(
            value: ValueCreate,
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
            gen.writeObjectField("type", value.type)
            gen.writeEndObject()
            gen.writeStartObject()
            gen.writeObjectField("condition", value.condition)
            gen.writeEndObject()
            gen.writeEndArray()
        }

    }
}