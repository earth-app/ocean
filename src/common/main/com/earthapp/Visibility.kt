package com.earthapp

import kotlinx.serialization.Serializable
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * Represents a type of visibility for a specific element.
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
@Serializable
enum class Visibility {
    /**
     * The element is private and not visible to others.
     */
    PRIVATE,
    /**
     * The element is public but not visible in search results.
     *
     * Unlisted accounts can still be recommended to others for
     * friending.
     */
    UNLISTED,
    /**
     * The element is public and visible to everyone.
     */
    PUBLIC
}