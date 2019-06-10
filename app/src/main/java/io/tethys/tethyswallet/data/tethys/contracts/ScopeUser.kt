package io.tethys.tethyswallet.data.tethys.contracts

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import io.tethys.tethyswallet.data.tethys.queries.QueryData

@JsonSerialize(using = ScopeUser.CustomSerializer::class)
data class ScopeUser(
    val amount: String, // __PINT__     amount of v-type variable for transfer
    val unit: String,   // __TINYTEXT__ unit type of v-type variable for transfer
    val pid: String,    // __BASE64__   id of v-type variable for transfer
    val tag: String,    // __XML__      condition for use of this v-type value
    override val contractType: ContractType = ContractType.SCOPE_USER
) : StandardContractInput() {
    class CustomSerializer : StdSerializer<ScopeUser>(
        ScopeUser::class.java
    ) {
        override fun serialize(
            value: ScopeUser,
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