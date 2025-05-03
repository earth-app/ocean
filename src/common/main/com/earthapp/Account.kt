package com.earthapp

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.Serializable
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * Represents an account in the Earth App.
 */
@OptIn(ExperimentalJsExport::class)
@Serializable
@JsExport
class Account {

    /**
     * The first name of the account holder.
     */
    var firstName: String = ""

    /**
     * Represents a type of account's visibility.
     */
    enum class Visibility {
        PRIVATE,
        UNLISTED,
        PUBLIC
    }

    /**
     * Represents a type of account's privacy settings.
     */
    enum class Privacy {
        PRIVATE,
        CIRCLE,
        MUTUAL,
        PUBLIC
    }

    companion object {
        private val logger = KotlinLogging.logger("com.earthapp.Account")

        /**
         * Represents the length of an account identifier.
         */
        const val ID_LENGTH = 24

        /**
         * Represents the characters that can be used in an account identifier.
         */
        val ID_CHARACTERS = ('a'..'z') + ('A'..'Z') + ('0'..'9') + listOf('@', '#')

        /**
         * Generates a new unique identifier for an account.
         * @return A unique identifier string.
         */
        fun newId(): String {
            val id = (1..ID_LENGTH)
                .map { ID_CHARACTERS.random() }
                .joinToString("")

            logger.debug { "Generated new Account ID: $id" }

            return id
        }
    }

}