package com.earthapp

import com.earthapp.account.Account
import com.earthapp.activity.Activity
import com.earthapp.event.Event
import com.earthapp.ocean.boat.Scraper
import korlibs.io.compression.compress
import korlibs.io.compression.deflate.GZIP
import korlibs.io.compression.uncompress
import korlibs.io.lang.Charsets
import korlibs.io.lang.decodeToString
import korlibs.io.lang.encodeToByteArray
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

internal val serializers = SerializersModule {
    polymorphic(Exportable::class) {
        subclass(Account.serializer())
        subclass(Event.serializer())
        subclass(Activity.serializer())
        subclass(Scraper.Page.serializer())
    }
}

internal val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    prettyPrint = true
    serializersModule = serializers
}

object CompressionSerializer : KSerializer<String> {
    private val charset = Charsets.UTF8

    override val descriptor = PrimitiveSerialDescriptor("CompressedString", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: String) {
        val bytes = value.encodeToByteArray(charset).compress(GZIP)
        encoder.encodeString(bytes.decodeToString(charset))
    }

    override fun deserialize(decoder: Decoder): String {
        val compressed = decoder.decodeString()
        val bytes = compressed.encodeToByteArray(charset).uncompress(GZIP)
        return bytes.decodeToString(charset)
    }
}