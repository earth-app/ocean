package com.earthapp.ocean

import com.earthapp.activity.Activity
import com.earthapp.event.Event
import com.earthapp.event.EventType
import com.earthapp.event.Location
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.test.Test
import kotlin.time.TimeSource

class TestRecommendEvents {

    val logger = KotlinLogging.logger("TestRecommendEvents")

    val users = listOf(
        "alice",
        "bob",
        "charlie",
        "dave",
        "eve",
        "frank",
        "grace",
        "heidi",
        "ivan",
        "judy",
        "kathy",
    )

    val userRelations = mapOf(
        "alice" to listOf("bob", "charlie", "dave"),
        "bob" to listOf("alice", "eve", "frank"),
        "charlie" to listOf("alice", "grace", "heidi", "ivan"),
        "dave" to listOf("bob", "ivan", "judy"),
        "eve" to listOf("charlie", "kathy", "judy"),
        "frank" to listOf("alice", "bob"),
        "grace" to listOf("heidi", "ivan", "judy"),
        "heidi" to listOf("charlie", "kathy"),
        "ivan" to listOf("dave", "judy"),
        "judy" to listOf("kathy", "alice", "bob", "charlie", "dave", "eve"),
        "kathy" to listOf("frank", "grace", "heidi", "ivan", "judy")
    )

    val userActivities = mapOf(
        "alice" to listOf("soccer", "knitting", "fencing"),
        "bob" to listOf("coding", "gardening", "american football"),
        "charlie" to listOf("baseball", "anime", "hiking"),
        "dave" to listOf("cooking", "painting", "yoga"),
        "eve" to listOf("photography", "music", "rock climbing"),
        "frank" to listOf("woodworking", "djing", "sleeping"),
        "grace" to listOf("surfing", "3D printing", "robotics"),
        "heidi" to listOf("soccer", "knitting", "fencing"),
        "ivan" to listOf("coding", "gardening", "american football"),
        "judy" to listOf("baseball", "anime", "hiking"),
        "kathy" to listOf("cooking", "painting", "yoga")
    ).flatMap { (key, activities) -> activities.map { key to Activity(it.lowercase(), it) }}.groupBy({ it.first }, { it.second })

    val events = listOf(
        Triple("soccer match", EventType.IN_PERSON, listOf("snacks", "music", "team photos")),
        Triple("knitting workshop", EventType.IN_PERSON, listOf("tea", "pattern sharing", "show and tell")),
        Triple("fencing tournament", EventType.IN_PERSON, listOf("medal ceremony", "refreshments", "photo booth")),
        Triple("coding bootcamp", EventType.HYBRID, listOf("networking", "Q&A", "project showcase")),
        Triple("gardening club", EventType.IN_PERSON, listOf("plant swap", "gardening tips", "refreshments")),
        Triple("american football game", EventType.IN_PERSON, listOf("tailgate", "cheerleading", "halftime show")),
        Triple("baseball practice", EventType.IN_PERSON, listOf("batting practice", "snacks", "team meeting")),
        Triple("anime screening", EventType.HYBRID, listOf("cosplay", "discussion", "fan art")),
        Triple("hiking trip", EventType.IN_PERSON, listOf("picnic", "nature talk", "group photo")),
        Triple("cooking class", EventType.ONLINE, listOf("ingredient prep", "live demo", "Q&A")),
        Triple("painting workshop", EventType.ONLINE, listOf("art critique", "materials list", "virtual gallery")),
        Triple("yoga session", EventType.HYBRID, listOf("meditation", "breathing exercises", "music")),
        Triple("photography walk", EventType.IN_PERSON, listOf("photo contest", "editing tips", "group photo")),
        Triple("music concert", EventType.HYBRID, listOf("meet and greet", "merchandise", "encore")),
        Triple("rock climbing expedition", EventType.IN_PERSON, listOf("safety briefing", "gear check", "group photo")),
        Triple("woodworking class", EventType.IN_PERSON, listOf("tool demo", "project showcase", "snacks")),
        Triple("djing event", EventType.IN_PERSON, listOf("dance floor", "light show", "guest DJ")),
        Triple("sleeping retreat", EventType.ONLINE, listOf("guided meditation", "sleep tips", "Q&A")),
        Triple("surfing lesson", EventType.IN_PERSON, listOf("warm-up", "safety talk", "group photo")),
        Triple("3D printing workshop", EventType.HYBRID, listOf("demo prints", "Q&A", "project showcase")),
        Triple("robotics competition", EventType.IN_PERSON, listOf("awards", "team presentations", "demo area")),
        Triple("soccer tournament", EventType.IN_PERSON, listOf("team jerseys", "snacks", "trophy ceremony")),
        Triple("knitting circle", EventType.IN_PERSON, listOf("yarn swap", "pattern sharing", "refreshments")),
        Triple("fencing exhibition", EventType.IN_PERSON, listOf("demonstration", "Q&A", "refreshments")),
        Triple("coding hackathon", EventType.HYBRID, listOf("team building", "prizes", "networking")),
        Triple("gardening festival", EventType.IN_PERSON, listOf("plant sale", "workshops", "refreshments")),
        Triple("music festival", EventType.IN_PERSON, listOf("live performances", "food stalls", "merchandise")),
        Triple("anime convention", EventType.IN_PERSON, listOf("cosplay contest", "panels", "merchandise")),
        Triple("baseball game", EventType.IN_PERSON, listOf("fan zone", "snacks", "team merchandise")),
    ).mapIndexed { i, (desc, type, activities) ->
        val host = users[i % users.size]

        Event(host) {
            name = desc
            this.type = type
            description = "Description for '$desc' event, which is going to be really fun. Join us!"
            date = TimeSource.Monotonic.markNow().elapsedNow().inWholeMilliseconds + (i * 1000 * 60 * 60.0)
            endDate = date + (2 * 1000L * 60 * 60) // 2 hours

            if (type != EventType.ONLINE) {
                val lat = 34.0 + ((i % 5) + (i / 9) * 0.1) * 0.063
                val lon = 12.0 + ((i % 5) + (i / 7) * 0.1) * 0.063

                location = Location(lat, lon)
            }

            attendees.addAll(
                userRelations[host]?.shuffled()?.take(3) ?: emptyList()
            )

            this.activities.add(description.substringAfterLast(' '))
            this.activities.addAll(activities)
        }
    }

    @Test
    fun testRecommendEvents() {
        for ((i, user) in users.withIndex()) {
            val lat = 34.0 + ((i % 6) + (i / 8) * 0.1) * 0.063
            val lon = 12.0 + ((i % 5) + (i / 5) * 0.1) * 0.063
            val userLocation = Location(lat, lon)

            val past = events.withIndex()
                .filter { (j, event) -> event.date < TimeSource.Monotonic.markNow().elapsedNow().inWholeMilliseconds && (j % 3) == (i % 3)  }
                .map { it.value }

            val future = events.withIndex()
                .filter { (j, event) -> event.date >= TimeSource.Monotonic.markNow().elapsedNow().inWholeMilliseconds && (j % 3) == (i % 3) }
                .map { it.value }

            val recommendedEvents = recommendEvents(
                userLocation,
                userRelations[user] ?: emptyList(),
                events,
                past,
                future,
                userActivities[user] ?: emptyList()
            )

            logger.info { "Recommended events for $user (${userActivities[user]?.joinToString { it.name }}):" }
            logger.debug { "= ($user is friends with: ${userRelations[user]?.joinToString()})" }
            recommendedEvents.forEach { event ->
                logger.debug { "| ${event.name} (${event.type})${if (event.type != EventType.ONLINE) " at ${event.location}" else ""}" }
            }
        }
    }

}