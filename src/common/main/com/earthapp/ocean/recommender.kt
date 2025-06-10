package com.earthapp.ocean

import com.earthapp.activity.Activity

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
fun recommendActivity(all: List<Activity>, current: List<Activity>): List<Activity> {
    val currentKeywords = current.flatMap { activity ->
        activity.name.split(Regex("\\W+")) + (activity.description ?: "").split(Regex("\\W+"))
    }.map { it.lowercase() }.filter { it.length > 2 }.toSet()

    val currentTypes = current.flatMap { it.types }.toSet()

    data class ScoredActivity(val activity: Activity, val keywordScore: Double, val typeScore: Double) {
        val totalScore: Double get() = keywordScore * 0.6 + typeScore * 0.4
    }

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

    // Sort by descending total score for most similar
    val sorted = scored.sortedByDescending { it.totalScore }

    // First recommendation: highest score
    val first = sorted.firstOrNull()?.activity

    // Second: pick one in the middle: moderately similar
    val second = if (sorted.size >= 3) sorted[sorted.size / 2].activity else sorted.drop(1).firstOrNull()?.activity

    // Third: completely different: zero keyword and type overlap, lowest total score
    val different = scored
        .filter { it.keywordScore == 0.0 && it.typeScore == 0.0 }
        .minByOrNull { it.totalScore }
        ?.activity
        ?: scored.lastOrNull()?.activity

    return listOfNotNull(first, second, different)
}