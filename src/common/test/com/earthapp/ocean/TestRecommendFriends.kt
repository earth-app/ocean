package com.earthapp.ocean

import com.earthapp.account.Account
import com.earthapp.activity.Activity
import com.earthapp.activity.ActivityType
import com.earthapp.event.Event
import com.earthapp.event.EventType
import com.earthapp.event.Location
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.time.TimeSource

class TestRecommendFriends {

    val logger = KotlinLogging.logger("TestRecommendFriends")

    // Create test activities
    val soccer = Activity(
        "soccer",
        "Soccer is a popular team sport played worldwide, known for its fast pace and emphasis on teamwork and strategy.",
        ActivityType.HOBBY, ActivityType.SPORT, ActivityType.OTHER
    )

    val coding = Activity(
        "coding",
        "Coding involves writing computer programs to solve problems or build applications.",
        ActivityType.HOBBY, ActivityType.WORK, ActivityType.STUDY
    )

    val gardening = Activity(
        "gardening",
        "Gardening is the practice of growing and cultivating plants for food, beauty, or relaxation.",
        ActivityType.HOBBY, ActivityType.RELAXATION, ActivityType.OTHER
    )

    val painting = Activity(
        "painting",
        "Painting is the art of applying color to surfaces using brushes or other tools.",
        ActivityType.HOBBY, ActivityType.RELAXATION, ActivityType.OTHER
    )

    val yoga = Activity(
        "yoga",
        "Yoga is a practice that combines physical postures, breathing exercises, and meditation for overall well-being.",
        ActivityType.HOBBY, ActivityType.RELAXATION, ActivityType.OTHER
    )

    val fencing = Activity(
        "fencing",
        "Fencing is a competitive sport involving swordplay and quick reflexes.",
        ActivityType.SPORT, ActivityType.HOBBY, ActivityType.OTHER
    )

    // Create test accounts with different activity combinations
    val alice = Account("alice") {
        activities.addAll(listOf(soccer, coding))
    }

    val bob = Account("bob") {
        activities.addAll(listOf(gardening, painting))
    }

    val charlie = Account("charlie") {
        activities.addAll(listOf(soccer, yoga))
    }

    val dave = Account("dave") {
        activities.addAll(listOf(coding, fencing))
    }

    val eve = Account("eve") {
        activities.addAll(listOf(painting, yoga))
    }

    val frank = Account("frank") {
        activities.addAll(listOf(soccer, gardening))
    }

    val grace = Account("grace") {
        activities.addAll(listOf(coding, painting))
    }

    val henry = Account("henry") {
        activities.addAll(listOf(fencing, yoga))
    }

    val ivy = Account("ivy") {
        activities.addAll(listOf(soccer, coding, gardening))
    }

    val jack = Account("jack") {
        activities.addAll(listOf(painting, fencing))
    }

    val allAccounts = listOf(alice, bob, charlie, dave, eve, frank, grace, henry, ivy, jack)

    init {
        alice.addFriends(bob, charlie)
        bob.addFriends(alice, dave)
        charlie.addFriends(alice, eve)
        dave.addFriends(bob, frank)
        eve.addFriends(charlie, grace)
        frank.addFriends(dave)
        grace.addFriends(eve)
    }

    // Create test events
    val events = listOf(
        Event("alice") {
            name = "Soccer match"
            type = EventType.IN_PERSON
            description = "A friendly soccer match in the park"
            date = TimeSource.Monotonic.markNow().elapsedNow().inWholeMilliseconds + 86400000.0 // 1 day
            endDate = date + 7200000L // 2 hours
            location = Location(34.05, 12.05)
            attendees.addAll(listOf("alice", "charlie", "frank"))
            activities.add("soccer")
        },
        Event("bob") {
            name = "Coding workshop"
            type = EventType.HYBRID
            description = "Learn new programming techniques"
            date = TimeSource.Monotonic.markNow().elapsedNow().inWholeMilliseconds + 172800000.0 // 2 days
            endDate = date + 10800000L // 3 hours
            location = Location(34.02, 12.02)
            attendees.addAll(listOf("bob", "dave", "grace"))
            activities.add("coding")
        },
        Event("eve") {
            name = "Art exhibition"
            type = EventType.IN_PERSON
            description = "Local artists showcasing their paintings"
            date = TimeSource.Monotonic.markNow().elapsedNow().inWholeMilliseconds + 259200000.0 // 3 days
            endDate = date + 14400000L // 4 hours
            location = Location(34.08, 12.08)
            attendees.addAll(listOf("eve", "jack", "grace"))
            activities.add("painting")
        }
    )

    @Test
    fun testRecommendFriendsBasic() {
        val userLocation = Location(34.0, 12.0)

        val recommendations = recommendFriends(alice, userLocation, allAccounts, events)

        assertTrue { recommendations.isNotEmpty() }
        assertTrue { recommendations.size <= 15 }
        assertTrue { alice !in recommendations }
        assertTrue { recommendations.none { alice.isFriendAdded(it) } }

        logger.info { "Recommended friends for Alice: ${recommendations.joinToString()}" }
    }

    @Test
    fun testFriendsOfFriends() {
        val userLocation = Location(34.0, 12.0)

        // Alice is friends with bob and charlie
        // Bob is friends with dave, Charlie is friends with eve
        // So dave and eve should be potential friends-of-friends recommendations
        val recommendations = recommendFriends(alice, userLocation, allAccounts, events)
        logger.debug { "Recommendations for Alice: ${recommendations.joinToString()}" }

        val friendsOfFriends = recommendations.filter { candidate ->
            alice.friends.any { friendId ->
                val friend = allAccounts.find { it.id == friendId }
                friend?.isFriendAdded(candidate) == true
            }
        }

        assertTrue { friendsOfFriends.isNotEmpty() }
        logger.info { "Friends of friends for Alice: ${friendsOfFriends.joinToString()}" }
    }

    @Test
    fun testActivitySimilarity() {
        val userLocation = Location(34.0, 12.0)

        // Alice has soccer and coding
        // Charlie has soccer and yoga
        // Dave has coding and fencing
        // Both should have some activity similarity
        val recommendations = recommendFriends(alice, userLocation, allAccounts, events)

        val activitySimilar = recommendations.filter { candidate ->
            val aliceActivities = alice.activities.flatMap { it.name.split(Regex("\\W+")) }.map { it.lowercase() }.toSet()
            val candidateActivities = candidate.activities.flatMap { it.name.split(Regex("\\W+")) }.map { it.lowercase() }.toSet()
            aliceActivities.intersect(candidateActivities).isNotEmpty()
        }

        assertTrue { activitySimilar.isNotEmpty() }
        logger.info { "Activity similar friends for Alice: ${activitySimilar.joinToString()}" }
    }

    @Test
    fun testEventSimilarity() {
        val userLocation = Location(34.0, 12.0)

        // Add Alice to some events to test event-based recommendations
        events[0].attendees.add("alice") // Soccer match
        events[1].attendees.add("alice") // Coding workshop

        val recommendations = recommendFriends(alice, userLocation, allAccounts, events)

        val eventSimilar = recommendations.filter { candidate ->
            events.any { event ->
                event.attendees.contains("alice") && event.attendees.contains(candidate.id)
            }
        }

        logger.info { "Event similar friends for Alice: ${eventSimilar.joinToString()}" }
    }

    @Test
    fun testLocationProximity() {
        val userLocation = Location(34.0, 12.0)

        val recommendations = recommendFriends(alice, userLocation, allAccounts, events)

        // All recommendations should be within reasonable distance based on events
        recommendations.forEach { candidate ->
            val candidateEvents = events.filter { it.attendees.contains(candidate.id) }
            if (candidateEvents.isNotEmpty()) {
                val avgDistance = candidateEvents.mapNotNull { it.location?.distanceTo(userLocation) }.average()
                assertTrue { avgDistance <= 25.0 || candidateEvents.isEmpty() }
            }
        }

        logger.info { "Location-based friends for Alice: ${recommendations.joinToString()}" }
    }

    @Test
    fun testRecommendationDiversity() {
        val userLocation = Location(34.0, 12.0)

        val recommendations = recommendFriends(alice, userLocation, allAccounts, events)

        // Should have diverse recommendation types
        val friendsOfFriends = recommendations.filter { candidate ->
            alice.friends.any { friendId ->
                val friend = allAccounts.find { it.id == friendId }
                friend?.isFriendAdded(candidate) == true
            }
        }

        val activitySimilar = recommendations.filter { candidate ->
            val aliceActivities = alice.activities.flatMap { it.types }.toSet()
            val candidateActivities = candidate.activities.flatMap { it.types }.toSet()
            aliceActivities.intersect(candidateActivities).isNotEmpty()
        }

        logger.info { "Recommendation breakdown for Alice:" }
        logger.info { "- Friends of friends: ${friendsOfFriends.size}" }
        logger.info { "- Activity similar: ${activitySimilar.size}" }
        logger.info { "- Total recommendations: ${recommendations.size}" }

        assertTrue { recommendations.distinctBy { it.id }.size == recommendations.size }
    }

    @Test
    fun testUserWithNoFriends() {
        val userLocation = Location(34.0, 12.0)

        // Henry has no friends, should still get recommendations
        val recommendations = recommendFriends(henry, userLocation, allAccounts, events)

        assertTrue { recommendations.isNotEmpty() }
        assertTrue { henry !in recommendations }

        logger.info { "Recommended friends for Henry (no current friends): ${recommendations.joinToString()}" }
    }

    @Test
    fun testUserWithManyActivities() {
        val userLocation = Location(34.0, 12.0)

        // Ivy has multiple activities (soccer, coding, gardening)
        val recommendations = recommendFriends(ivy, userLocation, allAccounts, events)

        assertTrue { recommendations.isNotEmpty() }

        // Should find people with overlapping activities
        val activityMatches = recommendations.filter { candidate ->
            val ivyActivityNames = ivy.activities.map { it.name.lowercase() }.toSet()
            val candidateActivityNames = candidate.activities.map { it.name.lowercase() }.toSet()
            ivyActivityNames.intersect(candidateActivityNames).isNotEmpty()
        }

        assertTrue { activityMatches.isNotEmpty() }
        logger.info { "Recommended friends for Ivy (many activities): ${recommendations.joinToString()}" }
    }
}