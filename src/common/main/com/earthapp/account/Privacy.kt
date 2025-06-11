package com.earthapp.account

import kotlinx.serialization.Serializable
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * Represents a type of account's privacy settings.
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
@Serializable
enum class Privacy {
    /**
     * The field is private and only visible to the owner.
     */
    PRIVATE,
    /**
     * The field is visible to a select group of friends.
     */
    CIRCLE,
    /**
     * The field is visible to all friends.
     */
    MUTUAL,
    /**
     * The field is visible to the public.
     */
    PUBLIC
}