package com.earthapp.activity

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalJsExport::class)
@JsExport
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
     * The activity is not specified or does not fit into any of the above categories.
     */
    OTHER

}