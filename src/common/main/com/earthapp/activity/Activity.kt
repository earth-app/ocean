package com.earthapp.activity

import kotlinx.serialization.Serializable

/**
 * Represents an activity in the Earth App.
 */
@Serializable
abstract class Activity {

    /**
     * The label for the activity. This represents the type of activity it is (e.g. sport, hobby, etc.).
     */
    abstract val label: String

}