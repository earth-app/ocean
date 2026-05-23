package com.earthapp

import com.earthapp.activity.Activity
import com.earthapp.ocean.boat.Scraper
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

internal val serializers = SerializersModule {
    polymorphic(Exportable::class) {
        subclass(Activity::class, Activity.serializer())
        subclass(Scraper.Page::class, Scraper.Page.serializer())
    }
}

internal val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    prettyPrint = true
    serializersModule = serializers
    encodeDefaults = true
    explicitNulls = false
}
