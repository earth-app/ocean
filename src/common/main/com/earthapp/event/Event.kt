package com.earthapp.event

import com.earthapp.Exportable
import com.earthapp.account.Account
import com.earthapp.util.newIdentifier
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.Serializable
import kotlin.js.ExperimentalJsExport
import kotlin.js.ExperimentalJsStatic
import kotlin.js.JsExport
import kotlin.js.JsStatic
import kotlin.time.Duration

/**
 * Represents an event in the Earth App.
 */
@OptIn(ExperimentalJsExport::class, ExperimentalJsStatic::class)
@Serializable
@JsExport
class Event(
    override val id: String
) : Exportable() {

    /**
     * The name of the event.
     */
    var name: String = ""

    /**
     * The description of the event.
     */
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

    internal constructor(apply: Event.() -> Unit) : this(newId()) {
        apply(this)
        validate()
    }

    override fun validate0() {
        require(name.isNotEmpty()) { "Name must not be empty." }
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