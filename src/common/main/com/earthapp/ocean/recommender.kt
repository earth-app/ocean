@file:JsExport
@file:OptIn(ExperimentalJsExport::class)

package com.earthapp.ocean

import com.earthapp.account.Account
import com.earthapp.activity.Activity
import com.earthapp.event.Event
import com.earthapp.event.EventType
import com.earthapp.event.Location
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.math.exp

//<editor-fold desc="Activity Recommendation Function">

@JsExport.Ignore
private data class ScoredActivity(val activity: Activity, val keywordScore: Double, val typeScore: Double) {
    val totalScore: Double get() = keywordScore * 0.6 + typeScore * 0.4
}

/**
 * Recommends three activities based on the current activities that the user is engaged in.
 * This function analyzes the current activities and suggests new ones that complement or enhance the user's experience.
 *
 * - The first activity is an activity that most closely matches the user's current activities.
 * - The second activity is an activity that may be similar to some of the activities but may be a bit different.
 * - The third activity is an activity that is completely different from the user's current activities, providing a fresh experience.
 *
 * This function is designed to help users discover new activities that they might enjoy based on their current interests and activities.
 * @param all A list of all available activities in the system.
 * @param current A list of currently active activities.
 * @return A list of recommended activities that the user might enjoy based on their current activities.
 */
fun recommendActivity(
    all: List<Activity>,
    current: List<Activity>
): List<Activity> {
    val currentKeywords = current.flatMap { activity ->
        activity.name.split(Regex("\\W+")) + (activity.description ?: "").split(Regex("\\W+"))
    }.map { it.lowercase() }.filter { it.length > 2 }.toSet()

    val currentTypes = current.flatMap { it.types }.toSet()

    val scored = all
        .filter { it.id !in current.map { c -> c.id } }
        .map { activity ->
            val candidateKeywords = (activity.name.split(Regex("\\W+")) + (activity.description ?: "").split(Regex("\\W+")))
                .map { it.lowercase() }
                .filter { it.length > 2 }
                .toSet()

            val intersection = currentKeywords.intersect(candidateKeywords).size.toDouble()
            val union = (currentKeywords + candidateKeywords).size.toDouble().coerceAtLeast(1.0)
            val keywordScore = intersection / union

            val sharedTypes = currentTypes.intersect(activity.types).size
            val typeScore = sharedTypes.toDouble() / (currentTypes.size.coerceAtLeast(1))

            ScoredActivity(activity, keywordScore, typeScore)
        }

    val sorted = scored.sortedByDescending { it.totalScore }
    val first = sorted.firstOrNull()?.activity
    val second = if (sorted.size >= 3) sorted[sorted.size / 2].activity else sorted.drop(1).firstOrNull()?.activity

    val different = scored
        .filter { it.keywordScore == 0.0 && it.typeScore == 0.0 }
        .minByOrNull { it.totalScore }
        ?.activity
        ?: scored.lastOrNull()?.activity

    return listOfNotNull(first, second, different)
}

/**
 * Recommends activities based on the current activities that the user is engaged in.
 * @param all A list of all available activities in the system.
 * @param account An [Account] object representing the user, which contains a list of activities
 * @see recommendActivity
 */
fun recommendActivityForAccount(
    all: List<Activity>,
    account: Account,
): List<Activity> = recommendActivity(all, account.activities)

//</editor-fold>

//<editor-fold desc="Event Recommendation Function">

private val eventWeights = mapOf(
    "friendHost" to 4.0,
    "futureHost" to 3.5,
    "futureAttendee" to 3.0,
    "sharedActivity" to 3.0,
    "activityKeyword" to 2.5,
    "friendAttendee" to 2.0,
    "typeScore" to 1.5,
    "locationProximity" to 1.2,
    "futureLocationBonus" to 1.0,
    "currentActivityIdBonus" to 2.5,
    "friendDensityBonus" to 0.5,
    "pastSimilarityPenalty" to -3.0
)

private fun Event.keywordSet(): Set<String> =
    (name.split(Regex("\\W+")) + description.split(Regex("\\W+")))
        .map { it.lowercase() }.filter { it.length > 2 }.toSet()

/**
 * Recommends events based on the current events that the user is engaged in.
 *
 * This function analyzes the current events and suggests five new events that complement or enhance the user's experience.
 * - The first two events are events that most closely match the user's current events.
 * - The third event is an event that may be similar to some of the events but may be a bit different.
 * - The fourth event is an event that is completely different from the user's current events, providing a fresh experience.
 * - The fifth event is a random event, however, is close in terms of location to the user's current events.
 *
 * This function is designed to help users discover new events that they might enjoy based on their current interests and events.
 * @param location A [Location] object representing the user's current location.
 * @param friends A list of all the user's friends, represented by [Account.id]
 * @param all A list of all available events within 25 miles that the user can attend, that will be taking place in the future within the next 30 days.
 * @param past 10 events that the user has attended in the past, that will be used to filter out events that are too similar to the user's past events.
 * @param future A list of future events the user as signed up for, with a maximum of 10 events.
 * @param activities A list of activities that the user is currently engaged in, which will be used to score the events based on shared activities.
 * @return A list of recommended events that the user might enjoy based on their current events and location.
 */
fun recommendEvents(
    location: Location,
    friends: List<String>,
    all: List<Event>,
    past: List<Event>,
    future: List<Event>,
    activities: List<Activity>
): List<Event> {
    // Build activity keyword bag from current activities
    val currentActivityKeywords = activities.flatMap {
        it.name.split(Regex("\\W+")) + (it.description ?: "").split(Regex("\\W+"))
    }.map { it.lowercase() }.filter { it.length > 2 }.toSet()

    val currentActivityIds = activities.map { it.id }.toSet()
    val futureHostIds = future.map { it.hostId }.toSet()
    val futureAttendeeIds = future.flatMap { it.attendees }.toSet()
    val futureLocations = future.mapNotNull { it.location }

    val pastHostIds = past.map { it.hostId }.toSet()
    val pastAttendeeIds = past.flatMap { it.attendees }.toSet()
    val pastLocations = past.mapNotNull { it.location }

    fun Event.score(): Double {
        var score = 0.0

        if (hostId in friends) score += eventWeights.getValue("friendHost")
        if (hostId in futureHostIds) score += eventWeights.getValue("futureHost")
        if (attendees.any { it in futureAttendeeIds }) score += eventWeights.getValue("futureAttendee")
        if (attendees.any { it in friends }) score += eventWeights.getValue("friendAttendee")

        // Shared Activity ID
        val sharedActivities = currentActivityIds.intersect(this.activities)
        val activityMatchRatio = sharedActivities.size.toDouble() / this.activities.size.coerceAtLeast(1)
        score += activityMatchRatio * eventWeights.getValue("sharedActivity")

        if (sharedActivities.isNotEmpty()) {
            score += eventWeights.getValue("currentActivityIdBonus")
        }

        // Keyword match
        val keywords = keywordSet()
        val keywordOverlap = currentActivityKeywords.intersect(keywords).size.toDouble()
        val keywordUnion = (currentActivityKeywords + keywords).size.toDouble().coerceAtLeast(1.0)
        val keywordJaccard = keywordOverlap / keywordUnion
        score += keywordJaccard * eventWeights.getValue("activityKeyword")

        // Type Scoring
        score += when (type) {
            EventType.IN_PERSON -> 1.0
            EventType.HYBRID -> 0.5
            EventType.ONLINE -> 0.0
        } * eventWeights.getValue("typeScore")

        // Location Proximity to User (with exponential decay)
        val baseLoc = this.location ?: location
        val proximityScore = location.distanceTo(baseLoc)
        if (proximityScore <= 25.0) {
            val locWeight = exp(-proximityScore / 10.0) * eventWeights.getValue("locationProximity")
            score += locWeight
        }

        // Bonus for being close to any Future Event Location
        val nearFutureEvent = futureLocations.any { it.distanceTo(baseLoc) < 10.0 }
        if (nearFutureEvent) score += eventWeights.getValue("futureLocationBonus")

        // Friend Density Boost
        val friendCount = attendees.count { it in friends }
        score += friendCount * eventWeights.getValue("friendDensityBonus")

        // Past Event Penalty
        val pastHost = hostId in pastHostIds
        val pastAttendees = attendees.any { it in pastAttendeeIds }
        val pastCloseLoc = pastLocations.any { it.distanceTo(baseLoc) < 5.0 }
        if (pastHost || pastAttendees || pastCloseLoc) {
            score += eventWeights.getValue("pastSimilarityPenalty")
        }

        return score
    }

    val filtered = all.filterNot { it.id in future.map { f -> f.id } || it.id in past.map { p -> p.id } }
    val scored = filtered.map { it to it.score() }.sortedByDescending { it.second }

    val topTwo = scored.take(2).map { it.first }
    val mid = scored.drop(2).take(1).map { it.first }

    val different = scored
        .filter { (_, s) -> s < 2.0 }
        .takeIf { it.isNotEmpty() }?.randomOrNull()?.first
        ?: scored.lastOrNull()?.first

    val nearby = filtered
        .filter { (it.location ?: location).distanceTo(location) < 10.0 }
        .shuffled()
        .firstOrNull()

    return listOfNotNull(*topTwo.toTypedArray(), *mid.toTypedArray(), different, nearby)
        .distinctBy { it.id }
}

//</editor-fold>