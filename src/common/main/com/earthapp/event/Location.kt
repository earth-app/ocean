package com.earthapp.event

import com.earthapp.util.toRadians
import kotlinx.serialization.Serializable
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.math.*

/**
 * Represents a location in the Earth App.
 */
@Serializable
@OptIn(ExperimentalJsExport::class)
@JsExport
class Location(
    /**
     * The latitude of the location, in degrees between -90 and 90.
     */
    val latitude: Double,
    /**
     * The longitude of the location, in degrees between -180 and 180.
     */
    val longitude: Double
) {

    /**
     * Returns a string representation of the location in degrees.
     */
    override fun toString(): String {
        val lat = if (latitude >= 0) "N" else "S"
        val lon = if (longitude >= 0) "E" else "W"

        return "${abs(latitude)}°$lat, ${abs(longitude)}°$lon"
    }

    /**
     * Returns a string representation of the location with degrees, minutes, and seconds.
     * This format is useful for precise geographical coordinates.
     * Example: "34°30'15"N, 58°25'30"E"
     * @return A string representation of the location with degrees, minutes, and seconds.
     */
    fun toStringWithTime(): String {
        val lat = if (latitude >= 0) "N" else "S"
        val lon = if (longitude >= 0) "E" else "W"

        val latDegrees = abs(latitude).toInt()
        val latMinutes = ((abs(latitude) - latDegrees) * 60).toInt()
        val latSeconds = ((abs(latitude) - latDegrees - latMinutes / 60.0) * 3600).toInt()
        val lonDegrees = abs(longitude).toInt()
        val lonMinutes = ((abs(longitude) - lonDegrees) * 60).toInt()
        val lonSeconds = ((abs(longitude) - lonDegrees - lonMinutes / 60.0) * 3600).toInt()

        return "${latDegrees}°${latMinutes}'${latSeconds}\"$lat, ${lonDegrees}°${lonMinutes}'${lonSeconds}\"$lon"
    }

    /**
     * Calculates the distance to another location using the Haversine formula.
     * The distance is returned in kilometers.
     * @param loc2 The other location to calculate the distance to.
     * @return The distance, in kilometers.
     */
    fun distanceTo(loc2: Location): Double {
        val phi1 = latitude.toRadians()
        val phi2 = loc2.latitude.toRadians()
        val dPhi = (loc2.latitude - latitude).toRadians()
        val dLambda = (loc2.longitude - longitude).toRadians()

        val a = sin(dPhi / 2).pow(2) + cos(phi1) * cos(phi2) * sin(dLambda / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return 6371e3 * c / 1000.0
    }

}