@file:JsExport
@file:OptIn(ExperimentalJsExport::class)

package com.earthapp.util

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.time.TimeSource

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

/**
 * Represents the length of an API key.
 */
const val API_KEY_LENGTH = 37

// EA25K{00-99}G{identifier}QL4DX

private val API_KEY_CHARACTERS = ('0'..'9') + ('a'..'f')

private fun newApiKeyIdentifier() = (1..ID_LENGTH)
    .map { API_KEY_CHARACTERS.random() }
    .joinToString("")

/**
 * Generates a new API key. This does not mean that the key is valid.
 * The key is generated using the same method as the identifier.
 * @return A new API key as a string.
 */
fun newApiKey() = "EA25K${(0..9).random()}${(0..9).random()}G${newApiKeyIdentifier()}QL4DX"
