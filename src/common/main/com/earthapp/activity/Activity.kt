package com.earthapp.activity

import com.earthapp.Exportable
import kotlinx.serialization.Serializable

/**
 * Represents an activity in the Earth App.
 */
@Serializable
abstract class Activity : Exportable {

    /**
     * The label for the activity. This represents the type of activity it is (e.g. sport, hobby, etc.).
     */
    abstract val label: String

}