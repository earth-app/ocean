package com.earthapp.account

import com.earthapp.Exportable
import com.earthapp.activity.Activity
import com.earthapp.util.EmailValidator
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
class Account : Exportable {

    /**
     * The unique identifier for the account.
     */
    var id: String = ""

    /**
     * The username associated with the account.
     */
    var username: String = ""

    /**
     * The first name of the account holder.
     */
    var firstName: String = ""

    /**
     * The last name of the account holder.
     */
    var lastName: String = ""

    /**
     * The email address associated with the account.
     */
    @Serializable(with = EmailValidator::class)
    var email: String = ""

    /**
     * The address of the account holder.
     */
    var address: String = ""

    /**
     * The country of the account holder.
     */
    var country: String = ""

    /**
     * The phone number associated with the account.
     */
    var phoneNumber: Int = 0

    /**
     * The activities associated with the account.
     */
    var activities: List<Activity> = emptyList()

    companion object {
        private val logger = KotlinLogging.logger("com.earthapp.account.Account")

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