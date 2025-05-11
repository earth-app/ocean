package com.earthapp.util

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

object EmailValidator : RegexValidatingSerializer(
    regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$".toRegex()
)

@OptIn(ExperimentalSerializationApi::class)
open class RegexValidatingSerializer(private val regex: Regex) : KSerializer<String> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("RegexValidatingString", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: String) {
        if (!regex.matches(value)) throw SerializationException("String '$value' does not match pattern '$regex'")
        encoder.encodeString(value)
    }

    override fun deserialize(decoder: Decoder): String {
        val value = decoder.decodeString()
        if (!regex.matches(value)) throw SerializationException("String '$value' does not match pattern '$regex'")
        return value
    }
}