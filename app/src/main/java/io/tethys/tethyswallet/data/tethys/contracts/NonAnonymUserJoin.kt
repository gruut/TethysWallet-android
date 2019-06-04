package io.tethys.tethyswallet.data.tethys.contracts

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.StdSerializer

@JsonSerialize(using = NonAnonymUserJoin.CustomSerializer::class)
data class NonAnonymUserJoin(
    val time: String,       // __DATETIME__     time to sigma
    val gender: String,     // __ENUMGENDER__   gender
    val birthday: String,   // __DATE__         birthday
    val iscType: String,    // __TINYTEXT__     type of indurstrial standard code
    val iscCode: String,    // __TINYTEXT__     indurstrial standard code
    val ageLimit: Int,      // __INT__          age limit for service
    val location: String,   // __TEXT__         address
    val sigma: String       // __BASE64__       signature by authority
) : StandardContractInput {
    class CustomSerializer : StdSerializer<NonAnonymUserJoin>(
        NonAnonymUserJoin::class.java
    ) {
        override fun serialize(
            value: NonAnonymUserJoin,
            gen: JsonGenerator,
            provider: SerializerProvider
        ) {
            gen.writeStartArray()
            gen.writeStartObject()
            gen.writeObjectField("time", value.time)
            gen.writeEndObject()
            gen.writeStartObject()
            gen.writeObjectField("gender", value.gender)
            gen.writeEndObject()
            gen.writeStartObject()
            gen.writeObjectField("birthday", value.birthday)
            gen.writeEndObject()
            gen.writeStartObject()
            gen.writeObjectField("isc_type", value.iscType)
            gen.writeEndObject()
            gen.writeStartObject()
            gen.writeObjectField("isc_code", value.iscCode)
            gen.writeEndObject()
            gen.writeStartObject()
            gen.writeObjectField("age_limit", value.ageLimit)
            gen.writeEndObject()
            gen.writeStartObject()
            gen.writeObjectField("location", value.location)
            gen.writeEndObject()
            gen.writeStartObject()
            gen.writeObjectField("sigma", value.sigma)
            gen.writeEndObject()
            gen.writeEndArray()
        }

    }
}