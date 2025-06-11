package com.earthapp.account

import kotlinx.serialization.Serializable
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * Enum class representing different types of accounts.
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
@Serializable
enum class AccountType {

    /**
     * Represents a free account type.
     */
    FREE,

    /**
     * Represents a paid account type.
     */
    PRO,

    /**
     * Represents a writer account type.
     */
    WRITER,

    /**
     * Represents an organizer account type.
     */
    ORGANIZER,

    /**
     * Represents an administrator account type.
     */
    ADMINISTRATOR

}