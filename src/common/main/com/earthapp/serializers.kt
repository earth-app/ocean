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