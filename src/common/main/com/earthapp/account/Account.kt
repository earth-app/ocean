package com.earthapp.account

import com.earthapp.Exportable
import com.earthapp.activity.Activity
import com.earthapp.util.EmailValidator
import com.earthapp.util.newIdentifier
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.Serializable
import kotlin.js.ExperimentalJsExport
import kotlin.js.ExperimentalJsStatic
import kotlin.js.JsExport
import kotlin.js.JsStatic

/**
 * Represents an account in the Earth App.
 */
@OptIn(ExperimentalJsExport::class, ExperimentalJsStatic::class)
@Serializable
@JsExport
class Account(
    override val id: String,

    /**
     * The username associated with the account.
     */
    val username: String
) : Exportable() {
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
    var address: String? = null

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
    val activities = mutableListOf<Activity>()

    internal constructor(username: String, apply: Account.() -> Unit) : this(newId(), username) {
        apply(this)
        validate()
    }

    override fun validate0() {
        require(username.isNotEmpty()) { "Username must not be empty." }
        require(firstName.isNotEmpty()) { "First name must not be empty." }
        require(lastName.isNotEmpty()) { "Last name must not be empty." }
        require(email.isNotEmpty()) { "Email must not be empty." }
        require(country.isNotEmpty()) { "Country must not be empty." }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Account

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    companion object {
        private val logger = KotlinLogging.logger("com.earthapp.account.Account")

        /**
         * Generates a new unique identifier for an account.
         * @return A unique identifier string.
         */
        @JsStatic
        fun newId(): String {
            val id = newIdentifier()
            logger.debug { "Generated new Account ID: $id" }
            return id
        }
    }



}