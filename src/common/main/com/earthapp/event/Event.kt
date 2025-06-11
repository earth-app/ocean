package com.earthapp.event

import com.earthapp.CompressionSerializer
import com.earthapp.Exportable
import com.earthapp.util.ID_LENGTH
import com.earthapp.util.newIdentifier
import io.github.oshai.kotlinlogging.KotlinLogging
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
    @Serializable(with = CompressionSerializer::class)
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
    var location: String = ""

    /**
     * The list of attendees for the event.
     */
    val attendees = mutableListOf<String>()

    /**
     * The list of activities associated with the event.
     */
    val activities = mutableListOf<String>()

    internal constructor(hostId: String, apply: Event.() -> Unit) : this(newId(), hostId) {
        apply(this)
        validate()
    }

    override fun validate0() {
        require(hostId.isNotEmpty()) { "Host ID must not be empty." }
        require(hostId.length == ID_LENGTH) { "Host ID must be exactly $ID_LENGTH characters long." }
        require(name.isNotEmpty()) { "Name must not be empty." }

        if (activities.isNotEmpty())
            require(activities.size <= 10) { "Event can have a maximum of 5 activities." }
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

    companion object {
        private val logger = KotlinLogging.logger("com.earthapp.event.Event")

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

}