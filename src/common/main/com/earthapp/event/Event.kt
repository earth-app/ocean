package com.earthapp.event

import com.earthapp.StringCompressionSerializer
import com.earthapp.Exportable
import com.earthapp.MutableListByteArrayCompressionSerializer
import com.earthapp.account.Account
import com.earthapp.Visibility
import com.earthapp.activity.Activity
import com.earthapp.util.newIdentifier
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.js.ExperimentalJsExport
import kotlin.js.ExperimentalJsStatic
import kotlin.js.JsExport
import kotlin.js.JsStatic

/**
 * Represents an event in the Earth App.
 */
@OptIn(ExperimentalJsExport::class, ExperimentalJsStatic::class)
@Serializable
@JsExport
class Event(
    override val id: String,
    /**
     * The unique identifier for the host of the event.
     */
    val hostId: String,
) : Exportable() {

    /**
     * The name of the event.
     */
    var name: String = ""

    /**
     * The description of the event.
     */
    @Serializable(with = StringCompressionSerializer::class)
    var description: String = ""

    /**
     * The date of the event in milliseconds since epoch.
     */
    var date: Double = 0.0

    /**
     * The end time of the event in milliseconds since epoch.
     */
    var endDate: Double = 0.0

    /**
     * The location of the event.
     */
    var location: Location? = null

    /**
     * The list of images associated with the event, stored as byte arrays.
     * The first image is considered the main image.
     */
    @Serializable(with = MutableListByteArrayCompressionSerializer::class)
    val images = mutableListOf<ByteArray>()

    /**
     * The type of the event.
     */
    @SerialName("event_type")
    var type: EventType = EventType.IN_PERSON

    /**
     * The visibility of the event.
     */
    var visibility: Visibility = Visibility.UNLISTED

    /**
     * The list of attendees for the event, mapped as [Account.id]
     */
    val attendees = mutableListOf<String>()

    /**
     * The list of activities associated with the event, mapped as [Activity.id].
     */
    val activities = mutableListOf<String>()

    internal constructor(hostId: String, apply: Event.() -> Unit) : this(newId(), hostId) {
        apply(this)
        validate()
    }

    /**
     * Edits the properties of the event.
     * @param name The name of the event.
     * @param description The description of the event.
     * @param date The date of the event in milliseconds since epoch.
     * @param endDate The end time of the event in milliseconds since epoch.
     * @param location The location of the event.
     * @param type The type of the event.
     * @param visibility The visibility of the event.
     */
    fun patch(
        name: String = this.name,
        description: String = this.description,
        date: Double = this.date,
        endDate: Double = this.endDate,
        location: Location? = this.location,
        type: EventType = this.type,
        visibility: Visibility = this.visibility,
    ): Event {
        this.name = name
        this.description = description
        this.date = date
        this.endDate = endDate
        this.location = location
        this.type = type
        this.visibility = visibility

        validate()
        return this
    }

    override fun validate0() {
        require(hostId.isNotEmpty()) { "Host ID must not be empty." }
        require(name.isNotEmpty()) { "Name must not be empty." }

        if (activities.isNotEmpty())
            require(activities.size <= MAX_ACTIVITIES) { "Event can have a maximum of $MAX_ACTIVITIES activities." }

        if (images.isNotEmpty())
            require(images.size <= MAX_IMAGES) { "Event can have a maximum of $MAX_IMAGES images." }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Event

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "$name <$id> (host=$hostId, date=$date)"
    }

    companion object {
        private val logger = KotlinLogging.logger("com.earthapp.event.Event")

        /**
         * The maximum number of activities an event can have.
         */
        const val MAX_ACTIVITIES = 25

        /**
         * The maximum number of images an event can have.
         * Note: The first image is considered the main image.
         */
        const val MAX_IMAGES = 15

        /**
         * Generates a new unique identifier for an event.
         * @return A new unique identifier as a string.
         */
        @JsStatic
        fun newId(): String {
            val id = newIdentifier()
            logger.debug { "Generated new event ID: $id" }
            return id
        }
    }

    /**
     * Checks if the event is visible to a given account.
     * @param account The account to check visibility for.
     * @return True if the event is visible to the account, false otherwise.
     */
    fun isVisibleTo(account: Account): Boolean {
        return when (visibility) {
            Visibility.PRIVATE -> account.id == hostId || attendees.contains(account.id)
            else -> true
        }
    }

    /**
     * Adds an image to the event.
     * @param image The image as a byte array to be added.
     */
    fun addImage(image: ByteArray) {
        if (images.size >= MAX_IMAGES) {
            logger.warn { "Event already has $MAX_IMAGES images, cannot add more: $this" }
            return
        }

        if (image.isEmpty()) {
            logger.warn { "Attempted to add an empty image to the event: $this" }
            return
        }

        images.add(image)
    }

    /**
     * Removes an image from the event.
     * @param image The image as a byte array to be removed.
     */
    fun removeImage(image: ByteArray) {
        if (images.isEmpty()) {
            logger.warn { "No images to remove from the event: $this" }
            return
        }

        if (images.remove(image)) {
            logger.debug { "Image removed successfully from event: $this" }
        } else {
            logger.warn { "Image not found in the event's image list: $this" }
        }
    }

    /**
     * Checks if an account is an attendee of the event.
     * @param account The account to check.
     * @return True if the account is an attendee, false otherwise.
     */
    fun isAttendee(account: Account): Boolean {
        return attendees.contains(account.id)
    }

    /**
     * Adds an attendee to the event.
     * @param account The account to be added as an attendee.
     * If the account is already an attendee, it will not be added again.
     */
    fun addAttendee(account: Account) {
        if (attendees.contains(account.id)) {
            logger.warn { "Account ${account.id} is already an attendee of the event: $this" }
            return
        }

        attendees.add(account.id)
        logger.debug { "Added account ${account.id} as an attendee to the event: $this" }
    }

    /**
     * Removes an attendee from the event.
     * @param account The account to be removed from the attendees.
     * If the account is not an attendee, it will log a warning.
     */
    fun removeAttendee(account: Account) {
        if (!attendees.remove(account.id)) {
            logger.warn { "Account ${account.id} is not an attendee of the event: $this" }
            return
        }

        logger.debug { "Removed account ${account.id} from the attendees of the event: $this" }
    }

    /**
     * Adds an activity to the event.
     * @param activity The activity to be added.
     */
    fun addActivity(activity: Activity) {
        if (activities.size >= MAX_ACTIVITIES) {
            logger.warn { "Event already has $MAX_ACTIVITIES activities, cannot add more: $this" }
            return
        }

        if (activities.contains(activity.id)) {
            logger.warn { "Activity ${activity.id} is already associated with the event: $this" }
            return
        }

        activities.add(activity.id)
        logger.debug { "Added activity ${activity.id} to the event: $this" }
    }

    /**
     * Removes an activity from the event.
     * @param activity The activity to be removed.
     * If the activity is not associated with the event, it will log a warning.
     */
    fun removeActivity(activity: Activity) {
        if (!activities.remove(activity.id)) {
            logger.warn { "Activity ${activity.id} is not associated with the event: $this" }
            return
        }

        logger.debug { "Removed activity ${activity.id} from the event: $this" }
    }

}