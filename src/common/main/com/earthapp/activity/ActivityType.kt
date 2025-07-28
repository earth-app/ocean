package com.earthapp.activity

import kotlinx.serialization.Serializable
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalJsExport::class)
@JsExport
@Serializable
enum class ActivityType {

    /**
     * The activity is related to a hobby.
     */
    HOBBY,

    /**
     * The activity is related to a sport.
     */
    SPORT,

    /**
     * The activity is related to work.
     */
    WORK,

    /**
     * The activity is related to study.
     */
    STUDY,

    /**
     * The activity is related to travel.
     */
    TRAVEL,

    /**
     * The activity is related to socializing.
     */
    SOCIAL,

    /**
     * The activity is related to relaxation.
     */
    RELAXATION,

    /**
     * The activity is related to health and fitness.
     */
    HEALTH,

    /**
     * The activity is related to a project.
     */
    PROJECT,

    /**
     * The activity is related to a personal goal.
     */
    PERSONAL_GOAL,

    /**
     * The activity is related to a community service or volunteering.
     */
    COMMUNITY_SERVICE,

    /**
     * The activity is related to a creative endeavor.
     */
    CREATIVE,

    /**
     * The activity is related to a family event or gathering.
     */
    FAMILY,

    /**
     * The activity is related to an event, celebration, or holiday.
     */
    HOLIDAY,

    /**
     * The activity is related to entertainment, such as watching movies, playing games, etc.
     */
    ENTERTAINMENT,

    /**
     * The activity is related to learning or acquiring new skills.
     */
    LEARNING,

    /**
     * The activity is related to nature, such as hiking, camping, etc.
     */
    NATURE,

    /**
     * The activity is related to technology, such as programming, gaming, etc.
     */
    TECHNOLOGY,

    /**
     * The activity is related to arts, such as painting, music, etc.
     */
    ART,

    /**
     * The activity is related to spirituality or religion.
     */
    SPIRITUALITY,

    /**
     * The activity is related to finance or investment.
     */
    FINANCE,

    /**
     * The activity is related to home improvement or DIY projects.
     */
    HOME_IMPROVEMENT,

    /**
     * The activity is related to pets or animals.
     */
    PETS,

    /**
     * The activity is related to fashion or personal grooming.
     */
    FASHION,

    /**
     * The activity is not specified or does not fit into any of the above categories.
     */
    OTHER

}