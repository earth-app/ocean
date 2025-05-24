@file:JsExport
@file:OptIn(ExperimentalJsExport::class)

import com.earthapp.Exportable

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