package com.earthapp.account

import com.earthapp.StringCompressionSerializer
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
import kotlin.js.JsName
import kotlin.js.JsStatic
import kotlin.reflect.KProperty

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
     * A short biography or description of the account holder.
     */
    @Serializable(with = StringCompressionSerializer::class)
    var bio: String = ""

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

    private val fieldPrivacy = mutableMapOf(
        "email" to Privacy.CIRCLE,
        "phoneNumber" to Privacy.PRIVATE,
        "address" to Privacy.PRIVATE,
        "activities" to Privacy.MUTUAL,
        "country" to Privacy.PUBLIC,
        "events" to Privacy.MUTUAL,
        "friends" to Privacy.MUTUAL,
        "last_login" to Privacy.CIRCLE
    )

    internal val friends = mutableSetOf<String>()

    internal constructor(username: String, apply: Account.() -> Unit) : this(newId(), "user-$username") {
        apply(this)
        firstName = username
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

        if (firstName.isNotEmpty())
            require(firstName.length in 1..30) { "First name must be between 2 and 30 characters long." }

        if (lastName.isNotEmpty())
            require(lastName.length in 1..30) { "Last name must be between 2 and 30 characters long." }

        if (email.isNotEmpty())
            require('@' in email) { "Email must contain '@' character." }

        if (country.isNotEmpty())
            require(country.length == 2) { "Country code must be exactly 2 characters long." }

        if (bio.isNotEmpty())
            require(bio.length <= 500) { "Bio must not exceed 300 characters." }
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

    override fun toString(): String {
        return "$firstName $lastName <$username>".trim()
    }

    companion object {
        private val logger = KotlinLogging.logger("com.earthapp.account.Account")

        private val neverPublic = listOf(
            "address", "phoneNumber"
        )

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

    /**
     * Checks if a field is private based on the specified privacy level.
     * @param field The name of the field to check.
     * @param level The requesting body's privacy level of access. For example, if it is your own account, it is [Privacy.PRIVATE] access.
     * If it is a mutual friend, it is [Privacy.MUTUAL] access. If it is a random account, it is [Privacy.PUBLIC] access.
     * @return Whether the requesting body does not have access to the field.
     */
    fun isFieldPrivate(field: String, level: Privacy): Boolean {
        val privacy = fieldPrivacy[field] ?: Privacy.PUBLIC
        return privacy.ordinal < level.ordinal
    }

    /**
     * Checks if a field is private based on the specified privacy level.
     * @param field The property to check.
     * @param level The requesting body's privacy level of access. For example, if it is your own account, it is [Privacy.PRIVATE] access.
     * If it is a mutual friend, it is [Privacy.MUTUAL] access. If it is a random account, it is [Privacy.PUBLIC] access.
     * @return Whether the requesting body does not have access to the field.
     */
    @JsExport.Ignore
    fun isFieldPrivate(field: KProperty<Any>, level: Privacy): Boolean {
        return isFieldPrivate(field.name, level)
    }

    /**
     * Gets the privacy level for a specific field.
     * @param field The name of the field to get privacy for.
     * @return The privacy level of the field.
     */
    fun getFieldPrivacy(field: String): Privacy {
        return fieldPrivacy[field] ?: Privacy.PUBLIC
    }

    /**
     * Gets the privacy level for a specific field.
     * @param field The name of the field to get privacy for.
     * @return The privacy level of the field.
     */
    @JsExport.Ignore
    fun getFieldPrivacy(field: KProperty<Any>): Privacy {
        return getFieldPrivacy(field.name)
    }

    /**
     * Sets the privacy level for a specific field.
     * @param field The name of the field to set privacy for.
     * @param privacy The privacy level to set for the field.
     */
    fun setFieldPrivacy(field: String, privacy: Privacy) {
        if (neverPublic.contains(field) && privacy == Privacy.PUBLIC) {
            logger.warn { "Attempted to set field '$field' to PUBLIC for '$username', which is not allowed." }
            return
        }

        fieldPrivacy[field] = privacy
    }

    /**
     * Sets the privacy level for a specific field.
     * @param field The property to set privacy for.
     * @param privacy The privacy level to set for the field.
     */
    @JsExport.Ignore
    fun setFieldPrivacy(field: KProperty<Any>, privacy: Privacy) {
        setFieldPrivacy(field.name, privacy)
    }

    /**
     * Checks if the account is visible to search and discovery.
     * @return True if the account is not private, false otherwise.
     */
    fun isVisible(): Boolean {
        return visibility != Visibility.PRIVATE
    }

    /**
     * Gets the count of friends associated with this account.
     * @return The number of friends.
     */
    fun getFriendCount(): Int {
        return friends.size
    }

    /**
     * Gets the identifiers of friends associated with this account.
     * @return A set of friend identifiers.
     */
    fun getFriendIds(): Set<String> {
        return friends.toSet()
    }

    /**
     * Checks if an account is marked as a friend.
     * @param account The account to check.
     * @return True if the account is marked as a friend, false otherwise.
     */
    fun isFriendAdded(account: Account): Boolean {
        return friends.contains(account.id)
    }

    /**
     * Checks if an account is a mutual friend.
     * Mutual friends are accounts that are friends with each other.
     * @param account The account to check.
     * @return True if the account is a mutual friend, false otherwise.
     */
    fun isMutualFriend(account: Account): Boolean {
        return friends.contains(account.id) && account.friends.contains(this.id)
    }

    /**
     * Adds a friend to the account.
     * @param account The account to add as a friend.
     * If the account is already a friend, it will not be added again.
     */
    fun addFriend(account: Account) {
        if (account.id == this.id) {
            logger.warn { "Account ${this.id} cannot add itself as a friend." }
            return
        }

        if (friends.contains(account.id)) {
            logger.warn { "Account ${this.id} already has ${account.id} as a friend." }
            return
        }

        friends.add(account.id)
    }

    /**
     * Adds multiple friends to the account.
     * @param accounts The accounts to add as friends.
     * If an account is already a friend, it will not be added again.
     */
    fun addFriends(vararg accounts: Account) {
        accounts.forEach { addFriend(it) }
    }

    /**
     * Adds multiple friends to the account.
     * @param accounts The collection of accounts to add as friends.
     * If an account is already a friend, it will not be added again.
     */
    @JsName("addFriendsList")
    fun addFriends(accounts: List<Account>) {
        accounts.forEach { addFriend(it) }
    }

}