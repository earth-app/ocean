@file:JsExport
@file:OptIn(ExperimentalJsExport::class)

package com.earthapp.ocean

import com.earthapp.activity.Activity
import com.earthapp.activity.ActivityType
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@JsExport.Ignore
private val WORD_SPLIT = Regex("\\W+")

@JsExport.Ignore
private fun Activity.keywordSet(): Set<String> {
    val name = name
    val description = description ?: ""
    val result = HashSet<String>(name.length / 4 + description.length / 4 + 8)
    fun add(text: String) {
        for (token in text.split(WORD_SPLIT)) {
            if (token.length > 2) result.add(token.lowercase())
        }
    }
    add(name)
    add(description)
    return result
}

@JsExport.Ignore
private data class ScoredActivity(val activity: Activity, val keywordScore: Double, val typeScore: Double) {
    val totalScore: Double get() = keywordScore * 0.6 + typeScore * 0.4
    val isNovel: Boolean get() = keywordScore == 0.0 && typeScore == 0.0
}

/**
 * Recommends three activities based on the current activities that the user is engaged in.
 *
 * - The first is the closest match to the user's current activities.
 * - The second is a related-but-different option (a tie-break between similarity and novelty).
 * - The third is intentionally different, providing a fresh experience.
 *
 * @param all A list of all available activities in the system.
 * @param current A list of currently active activities.
 * @return A list of recommended activities (up to three).
 */
fun recommendActivity(
    all: List<Activity>,
    current: List<Activity>
): List<Activity> {
    val currentIds = current.mapTo(HashSet(current.size)) { it.id }

    val currentKeywords = HashSet<String>()
    val currentTypes = HashSet<ActivityType>()
    for (activity in current) {
        currentKeywords.addAll(activity.keywordSet())
        currentTypes.addAll(activity.types)
    }
    val currentTypesSize = currentTypes.size.coerceAtLeast(1).toDouble()

    val scored = ArrayList<ScoredActivity>(all.size)
    for (activity in all) {
        if (activity.id in currentIds) continue

        val candidateKeywords = activity.keywordSet()

        var intersectionCount = 0
        for (k in candidateKeywords) if (k in currentKeywords) intersectionCount++
        val unionCount = (currentKeywords.size + candidateKeywords.size - intersectionCount).coerceAtLeast(1)
        val keywordScore = intersectionCount.toDouble() / unionCount.toDouble()

        var sharedTypes = 0
        for (t in activity.types) if (t in currentTypes) sharedTypes++
        val typeScore = sharedTypes.toDouble() / currentTypesSize

        scored.add(ScoredActivity(activity, keywordScore, typeScore))
    }

    val sorted = scored.sortedWith(
        compareByDescending<ScoredActivity> { it.totalScore }
            .thenByDescending { if (it.isNovel) 1 else 0 } // prefer novelty on tie
    )

    val first = sorted.firstOrNull()?.activity
    val second = when {
        sorted.size >= 3 -> sorted[sorted.size / 2].activity
        sorted.size == 2 -> sorted[1].activity
        else -> null
    }

    val different = scored
        .filter { it.keywordScore < 0.2 && it.typeScore < 0.2 }
        .shuffled()
        .firstOrNull()?.activity
        ?: scored.lastOrNull()?.activity

    return listOfNotNull(first, second, different)
}
