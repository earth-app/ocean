package com.earthapp.account

import com.earthapp.Exportable
import com.earthapp.activity.Activity
import com.earthapp.util.EmailValidator
import com.earthapp.util.newIdentifier
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.SerialName
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

    /**
     * The visibility of the account.
     */
    var visibility: Visibility = Visibility.UNLISTED

    /**
     * The type of the account.
     */
    @SerialName("account_type")
    var type: AccountType = AccountType.FREE

    /**
     * Checks if the account is of type [AccountType.ADMINISTRATOR].
     */
    var isAdmin: Boolean
        get() = type == AccountType.ADMINISTRATOR
        set(value) {
            if (value)
                type = AccountType.ADMINISTRATOR
            else if (type == AccountType.ADMINISTRATOR)
                type = AccountType.FREE // Reset to free if not admin
        }

    internal constructor(username: String, apply: Account.() -> Unit) : this(newId(), username) {
        apply(this)
        validate()
    }

    /**
     * Creates a new account instance with the specified properties.
     * @param username The username for the account.
     * @param firstName The first name of the account holder.
     * @param lastName The last name of the account holder.
     * @param email The email address associated with the account.
     * @param address The address of the account holder.
     * @param country The country of the account holder.
     * @param phoneNumber The phone number associated with the account.
     * @param visibility The visibility of the account.
     * @return A new instance of [Account] with the specified properties.
     */
    fun patch(
        username: String = this.username,
        firstName: String = this.firstName,
        lastName: String = this.lastName,
        email: String = this.email,
        address: String? = this.address,
        country: String = this.country,
        phoneNumber: Int = this.phoneNumber,
        visibility: Visibility = this.visibility,
        type: AccountType = this.type,
    ): Account {
        return Account(id, username).apply {
            this.firstName = firstName
            this.lastName = lastName
            this.email = email
            this.address = address
            this.country = country
            this.phoneNumber = phoneNumber
            this.visibility = visibility
            this.type = type
        }.also { it.validate() }
    }

    override fun validate0() {
        require(username.isNotEmpty()) { "Username must not be empty." }
        require(username.length in 4..20) { "Username must be between 4 and 20 characters long." }

        require(firstName.isNotEmpty()) { "First name must not be empty." }
        require(firstName.length in 1..30) { "First name must be between 2 and 30 characters long." }

        require(lastName.isNotEmpty()) { "Last name must not be empty." }
        require(lastName.length in 1..30) { "Last name must be between 2 and 30 characters long." }

        require(email.isNotEmpty()) { "Email must not be empty." }
        require('@' in email) { "Email must contain '@' character." }

        if (country.isNotEmpty())
            require(country.length == 2) { "Country code must be exactly 2 characters long." }
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