@file:OptIn(ExperimentalJsExport::class)

package com.earthapp

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@Serializable
@Polymorphic
@JsExport
abstract class Exportable {

    /**
     * The unique identifier for the object.
     */
    abstract val id: String

    /**
     * Validates its own state before storage. This method is not intended
     * to be overridden. Override the [validate0] method instead.
     */
    fun validate() {
        require(id.isNotEmpty()) { "ID must not be empty." }
        validate0()
    }

    internal abstract fun validate0()

    /**
     * Exports the object to a JSON string.
     * @return The string representation of the object.
     */
    fun toJson(): String = json.encodeToString(this)

    /**
     * Creates a deep copy of the object via a JSON round-trip.
     * @return A new instance of the object with the same properties.
     */
    fun deepCopy(): Exportable = json.decodeFromString<Exportable>(json.encodeToString(this))

    companion object {
        /**
         * Deserializes a JSON string into an [Exportable].
         */
        fun fromJson(jsonString: String): Exportable = json.decodeFromString(jsonString)
    }

}
