@file:JsExport
@file:OptIn(ExperimentalJsExport::class)

package com.earthapp.util

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * Represents the length of an identifier.
 */
const val ID_LENGTH = 24

/**
 * Represents the characters that can be used in an account identifier.
 */
@JsExport.Ignore
val ID_CHARACTERS = ('a'..'z') + ('A'..'Z') + ('0'..'9')

/**
 * Generates a new unique identifier.
 * @return A new unique identifier as a string.
 */
fun newIdentifier() = (1..ID_LENGTH)
    .map { ID_CHARACTERS.random() }
    .joinToString("")