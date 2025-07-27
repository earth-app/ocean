package com.earthapp.account

import com.earthapp.Exportable
import com.earthapp.Visibility
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
    @SerialName("phone_number")
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

    @SerialName("field_privacy")
    internal val fieldPrivacy = mutableMapOf(
        "email" to Privacy.CIRCLE,
        "phone_number" to Privacy.PRIVATE,
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
        bio: String = this.bio,
        email: String = this.email,
        address: String? = this.address,
        country: String = this.country,
        phoneNumber: Int = this.phoneNumber,
        visibility: Visibility = this.visibility,
    ): Account {
        this.firstName = firstName
        this.lastName = lastName
        this.email = email
        this.address = address
        this.country = country
        this.phoneNumber = phoneNumber
        this.visibility = visibility

        validate()
        return this
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
            require(bio.length <= 500) { "Bio must not exceed 500 characters." }
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

        private val allowedFields = listOf(
            "name",
            "bio",
            "phone_number",
            "country",
            "email",
            "address",
            "activities",
            "events",
            "friends",
            "last_login",
            "account_type"
        )

        private val neverPublic = listOf(
            "address", "phone_number"
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

        /**
         * Gets the list of allowed privacy fields for accounts.
         * These fields can be set to different privacy levels.
         * @return A list of allowed field names.
         */
        @JsStatic
        fun getAllowedPrivacyFields(): List<String> {
            return allowedFields
        }

        /**
         * Checks if a field is never allowed to be public.
         * This is used to prevent certain sensitive fields from being publicly accessible.
         * @param field The name of the field to check.
         * @return True if the field is never allowed to be public, false otherwise.
         */
        @JsStatic
        fun isNeverPublic(field: String): Boolean {
            return neverPublic.contains(field)
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
        if (!allowedFields.contains(field)) {
            logger.warn { "Attempted to get privacy for unknown field '$field' in account '$username'." }
            return Privacy.PUBLIC
        }

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

        if (!allowedFields.contains(field)) {
            logger.warn { "Attempted to set privacy for unknown field '$field' in account '$username'." }
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

    /**
     * Removes a friend from the account.
     * @param account The account to remove from friends.
     * If the account is not a friend, it will log a warning.
     */
    fun removeFriend(account: Account) {
        if (account.id == this.id) {
            logger.warn { "Account ${this.id} cannot remove itself as a friend." }
            return
        }

        if (!friends.remove(account.id)) {
            logger.warn { "Account ${this.id} does not have ${account.id} as a friend." }
            return
        }

        logger.debug { "Removed account ${account.id} from friends of account ${this.id}." }
    }

    /**
     * Removes a friend from the account by their ID.
     * @param id The ID of the account to remove from friends.
     * If the account is not a friend, it will log a warning.
     */
    @JsName("removeFriendsById")
    fun removeFriend(id: String) {
        val account = friends.find { it == id }
        if (account != null) {
            removeFriend(Account(id, ""))
        } else {
            logger.warn { "Account with ID $id is not a friend of account ${this.id}." }
        }
    }

    /**
     * Removes multiple friends from the account.
     * @param accounts The accounts to remove from friends.
     * If an account is not a friend, it will log a warning.
     */
    fun removeFriends(vararg accounts: Account) {
        accounts.forEach { removeFriend(it) }
    }

    /**
     * Removes multiple friends from the account.
     * @param accounts The collection of accounts to remove from friends.
     * If an account is not a friend, it will log a warning.
     */
    @JsName("removeFriendsList")
    fun removeFriends(accounts: List<Account>) {
        accounts.forEach { removeFriend(it) }
    }

    /**
     * Removes friends from the account by their IDs.
     * @param ids The IDs of the accounts to remove from friends.
     * If an account is not a friend, it will log a warning.
     */
    fun removeFriendsByIds(vararg ids: String) {
        ids.forEach { removeFriend(it) }
    }

    /**
     * Removes friends from the account by their IDs.
     * @param ids The collection of IDs of the accounts to remove from friends.
     * If an account is not a friend, it will log a warning.
     */
    @JsName("removeFriendsByIdsList")
    fun removeFriendsByIds(ids: List<String>) {
        ids.forEach { removeFriend(it) }
    }


    /**
     * Gets the count of activities associated with this account.
     * @return The number of activities.
     */
    fun getActivityCount(): Int {
        return activities.size
    }

    /**
     * Adds an activity to the account.
     * @param activity The activity to be added.
     * If the activity is already associated with the account, it will not be added again.
     */
    fun addActivity(activity: Activity) {
        if (activities.contains(activity)) {
            logger.warn { "Activity ${activity.id} is already associated with account ${this.id}." }
            return
        }

        activities.add(activity)
    }

    /**
     * Adds multiple activities to the account.
     * @param activities The activities to be added.
     * If an activity is already associated with the account, it will not be added again.
     */
    fun addActivities(vararg activities: Activity) {
        activities.forEach { addActivity(it) }
    }

    /**
     * Adds multiple activities to the account.
     * @param activities The collection of activities to be added.
     * If an activity is already associated with the account, it will not be added again.
     */
    @JsName("addActivitiesList")
    fun addActivities(activities: List<Activity>) {
        activities.forEach { addActivity(it) }
    }

    /**
     * Removes an activity from the account.
     * @param activity The activity to be removed.
     * If the activity is not associated with the account, it will log a warning.
     */
    fun removeActivity(activity: Activity) {
        if (!activities.remove(activity)) {
            logger.warn { "Activity ${activity.id} is not associated with account ${this.id}." }
            return
        }

        activities.remove(activity)
        logger.debug { "Removed activity ${activity.id} from account ${this.id}." }
    }

    /**
     * Removes an activity from the account by its ID.
     * @param id The ID of the activity to be removed.
     * If the activity is not associated with the account, it will log a warning.
     */
    @JsName("removeActivityById")
    fun removeActivity(id: String) {
        val activity = activities.find { it.id == id }
        if (activity != null) {
            removeActivity(activity)
        } else {
            logger.warn { "Activity with ID $id is not associated with account ${this.id}." }
        }
    }

    /**
     * Removes multiple activities from the account.
     * @param activities The activities to be removed.
     * If an activity is not associated with the account, it will log a warning.
     */
    fun removeActivities(vararg activities: Activity) {
        activities.forEach { removeActivity(it) }
    }

    /**
     * Removes multiple activities from the account.
     * @param activities The collection of activities to be removed.
     * If an activity is not associated with the account, it will log a warning.
     */
    @JsName("removeActivitiesList")
    fun removeActivities(activities: List<Activity>) {
        activities.forEach { removeActivity(it) }
    }

    /**
     * Removes activities from the account by their IDs.
     * @param ids The IDs of the activities to be removed.
     * If an activity is not associated with the account, it will log a warning.
     */
    fun removeActivitiesByIds(vararg ids: String) {
        ids.forEach { removeActivity(it) }
    }

    /**
     * Removes activities from the account by their IDs.
     * @param ids The collection of IDs of the activities to be removed.
     * If an activity is not associated with the account, it will log a warning.
     */
    @JsName("removeActivitiesByIdsList")
    fun removeActivitiesByIds(ids: List<String>) {
        ids.forEach { removeActivity(it) }
    }

    /**
     * Sets the activities associated with this account.
     * @param activities The activities to set.
     */
    fun setActivities(vararg activities: Activity) {
        this.activities.clear()
        this.activities.addAll(activities)
    }

    /**
     * Sets the activities associated with this account.
     * @param activities The list of activities to set.
     */
    @JsName("setActivitiesList")
    fun setActivities(activities: List<Activity>) {
        this.activities.clear()
        this.activities.addAll(activities)
    }

}