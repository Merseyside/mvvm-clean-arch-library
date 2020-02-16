package com.merseyside.mvvmcleanarch.data.serialization

import kotlinx.serialization.Decoder
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationException
import kotlinx.serialization.UpdateNotSupportedException
import kotlinx.serialization.internal.SerialClassDescImpl
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonInput
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.content

object JsonArrayToStringMapDeserializer : DeserializationStrategy<Map<String, String>> {

    override val descriptor = SerialClassDescImpl("JsonMap")

    override fun deserialize(decoder: Decoder): Map<String, String> {

        val input = decoder as? JsonInput ?: throw SerializationException("Expected Json Input")
        val array = input.decodeJson() as? JsonArray ?: throw SerializationException("Expected JsonArray")

        return array.map {
            it as JsonObject
            val firstKey = it.keys.first()
            firstKey to it[firstKey]!!.content
        }.toMap()


    }

    override fun patch(decoder: Decoder, old: Map<String, String>): Map<String, String> =
        throw UpdateNotSupportedException("Update not supported")

}