package com.earthapp.util

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.Level
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

private val log = mutableListOf<String>()

/**
 * Retrieves the in-memory logs as a string.
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
fun getInMemoryLog() = log.joinToString("\n")

internal fun KLogger.registerInMemory() = apply {
    for (level in Level.entries)
        at(level, null) {
            val message = StringBuilder("[${level.name}] $name - $message")
            payload?.let { message.append(" ($payload)") }
            cause?.let { message.append("\n$it") }

            log.add(message.toString())
        }

    debug { "Registered logger '$name' for in-memory logs" }
}