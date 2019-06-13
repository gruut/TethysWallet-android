package io.tethys.tethyswallet.data.grpc.message.response

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.fasterxml.jackson.databind.type.TypeFactory
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.tethys.tethyswallet.utils.ext.getTimestamp

@JsonDeserialize(using = MsgResult.CustomDeserializer::class)
data class MsgResult(
    @JsonIgnore
    override val time: String,
    @JsonIgnore
    override val merger: String,
    val jsonStr: String
) : MsgBody {

    @Suppress("UNCHECKED_CAST")
    class CustomSerializer : StdSerializer<List<List<Pair<String, Any>>>>(
        List::class.java as Class<List<List<Pair<String, Any>>>>
    ) {
        override fun serialize(
            value: List<List<Pair<String, Any>>>,
            gen: JsonGenerator,
            provider: SerializerProvider?
        ) {
            gen.writeStartArray()
            value.forEach { list: List<Pair<String, Any>> ->
                if (list.isNotEmpty()) {
                    gen.writeStartObject()
                    list.forEach { pair: Pair<String, Any> ->
                        gen.writeObjectField(pair.first, pair.second)
                    }
                    gen.writeEndObject()
                }
            }
            gen.writeEndArray()
        }

    }

    class CustomDeserializer : StdDeserializer<MsgResult>(MsgResult::class.java) {
        private val objectMapper = jacksonObjectMapper()

        init {
            SimpleModule().apply {
                addSerializer(CustomSerializer())
                objectMapper.registerModule(this)
            }
        }

        override fun deserialize(p: JsonParser, ctxt: DeserializationContext): MsgResult {
            val node: JsonNode = p.codec.readTree(p)
            val nameNode = node.path("name")
            val dataNode = node.path("data")

            val nameType = TypeFactory.defaultInstance()
                .constructCollectionType(List::class.java, String::class.java)
            val dataType = TypeFactory.defaultInstance()
                .constructCollectionType(List::class.java, List::class.java)

            val name: List<String> = objectMapper.readerFor(nameType).readValue(nameNode)
            val data: List<List<Any>> = objectMapper.readerFor(dataType).readValue(dataNode)

            // 이차원 배열로 재조합하여 Serialize
            val pairList: MutableList<MutableList<Pair<String, Any>>> = mutableListOf()
            for (i in data.indices) {
                pairList.add(mutableListOf())
                for (j in data[i].indices) {
                    pairList[i].add(Pair(name[j], data[i][j]))
                }
            }

            return MsgResult(
                getTimestamp().toString(),
                "",
                objectMapper.writeValueAsString(pairList)
            )
        }
    }
}