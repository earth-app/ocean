package com.earthapp

import com.earthapp.account.Account
import com.earthapp.activity.Activity
import com.earthapp.event.Event
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

internal val serializers = SerializersModule {
    polymorphic(Exportable::class) {
        subclass(Account.serializer())
        subclass(Event.serializer())
        subclass(Activity.serializer())
    }
}

internal val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    prettyPrint = true
    serializersModule = serializers
}