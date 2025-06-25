@file:JsExport
@file:OptIn(ExperimentalJsExport::class)

import com.earthapp.Exportable
import com.earthapp.ocean.boat.RSS

// com.earthapp.Exportable

/**
 * Converts a JSON string into an [Exportable] object.
 * @param json The JSON string to convert.
 * @return The [Exportable] object.
 */
fun fromJson(json: String): Exportable = Exportable.fromJson(json)

/**
 * Converts a binary array into an [Exportable] object.
 * @param binary The binary array to convert.
 * @return The [Exportable] object.
 */
fun fromBinary(binary: ByteArray): Exportable = Exportable.fromBinary(binary)

// com.earthapp.ocean.boat.RSS

/**
 * Creates an [RSS] object with the specified name, URL, and tags.
 * @param name The name of the RSS feed.
 * @param url The URL of the RSS feed.
 * @param tags The tags associated with the RSS feed.
 * @return The created [RSS] object.
 */
fun createRSS(name: String, url: String, tags: Array<String>): RSS = RSS(name, url, tags.toList())