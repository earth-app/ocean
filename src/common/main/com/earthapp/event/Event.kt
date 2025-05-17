package com.earthapp.event

import com.earthapp.Exportable
import com.earthapp.util.newIdentifier
import com.earthapp.util.registerInMemory
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.js.ExperimentalJsExport
import kotlin.js.ExperimentalJsStatic
import kotlin.js.JsExport
import kotlin.js.JsStatic

/**
 * Represents an event in the Earth App.
 */
@OptIn(ExperimentalJsExport::class, ExperimentalJsStatic::class)
@JsExport
class Event(
    override val id: String
) : Exportable {

    /**
     * The name of the event.
     */
    var name: String = ""

    /**
     * The list of attendees for the event.
     */
    val attendees = mutableListOf<String>()

    override fun validate0() {
        require(name.isNotEmpty()) { "Name must not be empty." }
    }

    companion object {
        private val logger = KotlinLogging.logger("com.earthapp.event.Event").registerInMemory()

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