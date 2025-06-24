package com.earthapp.ocean

import com.earthapp.activity.Activity
import com.earthapp.activity.ActivityType
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.test.Test
import kotlin.test.assertTrue

class TestRecommendActivity {

    val logger = KotlinLogging.logger("TestRecommendActivity")

    val soccer = Activity(
        "soccer",
        "Soccer is a popular team sport played worldwide, known for its fast pace and emphasis on teamwork and strategy.",
        ActivityType.HOBBY, ActivityType.SPORT, ActivityType.OTHER
    )

    val knitting = Activity(
        "knitting",
        "Knitting is a relaxing craft involving creating fabric from yarn using needles.",
        ActivityType.HOBBY, ActivityType.RELAXATION, ActivityType.OTHER
    )

    val fencing = Activity(
        "fencing",
        "Fencing is a competitive sport involving swordplay and quick reflexes.",
        ActivityType.SPORT, ActivityType.HOBBY, ActivityType.OTHER
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

    val americanFootball = Activity(
        "american football",
        "American football is a team sport known for its strategy, physicality, and popularity in the United States.",
        ActivityType.SPORT, ActivityType.HOBBY, ActivityType.OTHER
    )

    val baseball = Activity(
        "baseball",
        "Baseball is a bat-and-ball sport played between two teams, popular in the United States and Japan.",
        ActivityType.SPORT, ActivityType.HOBBY, ActivityType.OTHER
    )

    val anime = Activity(
        "anime",
        "Anime refers to Japanese animated shows and movies, enjoyed by fans worldwide.",
        ActivityType.HOBBY, ActivityType.RELAXATION, ActivityType.OTHER
    )

    val writing = Activity(
        "writing",
        "Writing involves creating stories, articles, or other texts for communication or expression.",
        ActivityType.HOBBY, ActivityType.WORK, ActivityType.STUDY
    )

    val threeDPrinting = Activity(
        "3D printing",
        "3D printing is the process of creating three-dimensional objects from digital models.",
        ActivityType.HOBBY, ActivityType.WORK, ActivityType.OTHER
    )

    val hardwareEngineering = Activity(
        "hardware engineering",
        "Hardware engineering involves designing and building physical computer components.",
        ActivityType.WORK, ActivityType.HOBBY, ActivityType.STUDY
    )

    val softball = Activity(
        "softball",
        "Softball is a team sport similar to baseball, played with a larger ball on a smaller field.",
        ActivityType.SPORT, ActivityType.HOBBY, ActivityType.OTHER
    )

    val drawing = Activity(
        "drawing",
        "Drawing is the art of creating images using pencils, pens, or other tools.",
        ActivityType.HOBBY, ActivityType.RELAXATION, ActivityType.OTHER
    )

    val sleeping = Activity(
        "sleeping",
        "Sleeping is a natural state of rest essential for health and well-being.",
        ActivityType.RELAXATION, ActivityType.OTHER
    )

    val surfing = Activity(
        "surfing",
        "Surfing is a water sport where individuals ride waves on a surfboard.",
        ActivityType.SPORT, ActivityType.HOBBY, ActivityType.RELAXATION
    )

    val woodWorking = Activity(
        "wood working",
        "Wood working is the craft of creating objects from wood using tools and techniques.",
        ActivityType.HOBBY, ActivityType.OTHER
    )

    val djing = Activity(
        "DJ-ing",
        "DJ-ing involves mixing and playing music for an audience, often at events or clubs.",
        ActivityType.HOBBY, ActivityType.SOCIAL, ActivityType.OTHER
    )

    val painting = Activity(
        "painting",
        "Painting is the art of applying color to surfaces using brushes or other tools.",
        ActivityType.HOBBY, ActivityType.RELAXATION, ActivityType.OTHER
    )

    val rockClimbing = Activity(
        "rock climbing",
        "Rock climbing is an outdoor activity that involves climbing up, down, or across natural rock formations.",
        ActivityType.SPORT, ActivityType.HOBBY, ActivityType.RELAXATION
    )

    val yoga = Activity(
        "yoga",
        "Yoga is a practice that combines physical postures, breathing exercises, and meditation for overall well-being.",
        ActivityType.HOBBY, ActivityType.RELAXATION, ActivityType.OTHER
    )

    val activities = listOf(
        soccer, knitting, fencing, coding, gardening, americanFootball,
        baseball, anime, writing, threeDPrinting, hardwareEngineering,
        softball, drawing, sleeping, surfing, woodWorking, djing,
        painting, rockClimbing, yoga
    )

    @Test
    fun testRecommendSports() {
        val sports = recommendActivity(
            activities,
            listOf(soccer, americanFootball, softball)
        )
        assertTrue { ActivityType.SPORT in sports[0].types } // ensure similar
        assertTrue { ActivityType.SPORT !in sports[2].types } // ensure different

        logger.info { "Recommended sports: ${sports.joinToString { it.name }}" }
    }

    @Test
    fun testRecommendHobbies() {
        val hobbies = recommendActivity(
            activities,
            listOf(coding, gardening, anime)
        )
        assertTrue { ActivityType.HOBBY in hobbies[0].types } // ensure similar

        logger.info { "Recommended hobbies: ${hobbies.joinToString { it.name }}" }
    }

    @Test
    fun testRecommendRelaxation() {
        val relaxation = recommendActivity(
            activities,
            listOf(knitting, drawing, yoga)
        )
        assertTrue { ActivityType.RELAXATION in relaxation[0].types } // ensure similar

        logger.info { "Recommended relaxation activities: ${relaxation.joinToString { it.name }}" }
    }
}