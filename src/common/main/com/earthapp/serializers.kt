package com.earthapp

import com.earthapp.account.Account
import com.earthapp.activity.Activity
import com.earthapp.event.Event
import com.earthapp.ocean.boat.Scraper
import korlibs.io.compression.compress
import korlibs.io.compression.deflate.GZIP
import korlibs.io.compression.uncompress
import korlibs.io.lang.Charsets
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.Json.Default.serializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kotlinx.serialization.serializer

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
    encodeDefaults = true
}

internal abstract class CompressionSerializer<T> : KSerializer<T> {
    override fun serialize(encoder: Encoder, value: T) {
        val bytes = encode(value).compress(GZIP)
        encoder.encodeSerializableValue(serializersModule.serializer(), bytes)
    }

    override fun deserialize(decoder: Decoder): T {
        val compressed = decoder.decodeSerializableValue(serializersModule.serializer<ByteArray>())
        if (compressed.isEmpty()) throw IllegalArgumentException("Compressed data found to be empty")

        val decompressed = compressed.uncompress(GZIP)
        return decode(decompressed)
    }

    abstract fun encode(value: T): ByteArray
    abstract fun decode(array: ByteArray): T
}

internal class StringCompressionSerializer : CompressionSerializer<String>() {
    private val charset = Charsets.UTF8

    override val descriptor = PrimitiveSerialDescriptor("CompressedString", PrimitiveKind.STRING)

    override fun encode(value: String): ByteArray = value.encodeToByteArray()
    override fun decode(array: ByteArray): String = array.decodeToString()
}

internal class MutableListByteArrayCompressionSerializer : CompressionSerializer<MutableList<ByteArray>>() {
    override val descriptor = PrimitiveSerialDescriptor("CompressedMutableListByteArray", PrimitiveKind.STRING)

    override fun encode(value: MutableList<ByteArray>): ByteArray {
        return value.joinToString(",") { it.decodeToString() }.encodeToByteArray()
    }

    override fun decode(array: ByteArray): MutableList<ByteArray> {
        return array.decodeToString().split(",").map { it.encodeToByteArray() }.toMutableList()
    }
}