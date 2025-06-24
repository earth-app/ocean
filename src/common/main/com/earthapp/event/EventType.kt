package com.earthapp.event

import kotlinx.serialization.Serializable
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * Represents an event type in the Earth App.
 */
@OptIn(ExperimentalJsExport::class)
@Serializable
@JsExport
enum class EventType {

    /**
     * This is an in-person event, where participants gather at a physical location.
     */
    IN_PERSON,

    /**
     * This is an online event, where participants join remotely, typically via a video conferencing platform.
     */
    ONLINE,

    /**
     * This is a hybrid event, combining both in-person and online participation.
     */
    HYBRID,

}