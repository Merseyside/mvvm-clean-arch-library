package com.upstream.basemvvmimpl.data

import kotlinx.serialization.*
import kotlinx.serialization.json.Json

@UseExperimental(UnstableDefault::class)
val json: Json by lazy {
    Json {
        strictMode = false
        allowStructuredMapKeys = true
    }
}

@UseExperimental(ImplicitReflectionSerializer::class)
inline fun <reified T : Any> T.serialize(): String {
    return json.stringify(this)
}

@UseExperimental(ImplicitReflectionSerializer::class)
inline fun <reified T : Any> String.deserialize(): T {
    return json.parse(this)
}

fun <T : Any> T.serialize(serializationStrategy: SerializationStrategy<T>): String {
    return json.stringify(serializationStrategy, this)
}

fun <T> String.deserialize(deserializationStrategy: DeserializationStrategy<T>): T {
    return json.parse(deserializationStrategy, this)
}

@UseExperimental(ImplicitReflectionSerializer::class)
inline fun <reified T : Any> T.serializePrimitive(): String {
    return json.stringify(this)
}

@UseExperimental(ImplicitReflectionSerializer::class)
inline fun <reified T : Any> String.deserializePrimitive(): T {
    return json.parse(this)
}
