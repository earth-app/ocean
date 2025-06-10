package com.earthapp.activity

import com.earthapp.CompressionSerializer
import com.earthapp.Exportable
import kotlinx.serialization.Serializable
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * Represents an activity in the Earth App.
 */
@OptIn(ExperimentalJsExport::class)
@Serializable
@JsExport
class Activity(
    /**
     * A human-readable name for the activity.
     */
    override val id: String,
    /**
     * The name of the activity.
     */
    val name: String,
) : Exportable() {

    /**
     * The description of the activity.
     */
    @Serializable(with = CompressionSerializer::class)
    val description: String? = null

    /**
     * The type of the activity.
     */
    val types = mutableListOf<ActivityType>()

    override fun validate0() {
        require(id.isNotEmpty()) { "ID must not be empty." }
        require(name.isNotEmpty()) { "Name must not be empty." }
        require(types.isNotEmpty()) { "Activity types must not be empty." }
        require(types.size <= 3) { "Activity can have a maximum of 3 types." }

        if (!description.isNullOrEmpty())
            require(description.length in 0..1000) { "Description must be between 0 and 1000 characters." }
    }

}