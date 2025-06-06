@file:OptIn(ExperimentalJsExport::class)

package com.earthapp

import io.github.oshai.kotlinlogging.KotlinLogging
import korlibs.io.compression.compress
import korlibs.io.compression.deflate.GZIP
import korlibs.io.compression.uncompress
import korlibs.io.lang.Charsets
import korlibs.io.lang.decodeToString
import korlibs.io.lang.encodeToByteArray
import kotlinx.serialization.Serializable
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@Serializable
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
     * Exports the object to a string format.
     * @return The string representation of the object.
     */
    fun toJson(): String = json.encodeToString(this)

    /**
     * Exports the object to a binary format.
     * @return The binary representation of the object.
     */
    fun toBinary(): ByteArray {
        val bytes = toJson().encodeToByteArray(Charsets.UTF8)
        return bytes.compress(GZIP)
    }

    /**
     * Creates a deep copy of the object.
     * @return A new instance of the object with the same properties.
     */
    fun deepCopy(): Exportable = json.decodeFromString<Exportable>(json.encodeToString(this))

    companion object {
        private val logger = KotlinLogging.logger("com.earthapp.Exportable")

        /**
         * Deserializes a JSON string into an object of the specified type.
         */
        fun fromJson(jsonString: String): Exportable = json.decodeFromString(jsonString)

        /**
         * Deserializes a binary string into an object of the specified type.
         */
        fun fromBinary(binary: ByteArray): Exportable {
            val decompressed = binary.uncompress(GZIP)
            val jsonString = decompressed.decodeToString(Charsets.UTF8)
            return fromJson(jsonString)
        }
    }

}