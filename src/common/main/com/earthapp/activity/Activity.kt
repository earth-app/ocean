package com.earthapp.activity

import com.earthapp.Exportable
import kotlinx.serialization.SerialName
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
    override val id: String,
    var name: String,
) : Exportable() {

    var description: String? = null

    val aliases = mutableListOf<String>()

    @SerialName("activity_types")
    val types = mutableListOf<ActivityType>()

    internal constructor(name: String, description: String? = null, vararg types: ActivityType) : this(name.lowercase(), name) {
        this.description = description
        this.types.addAll(types)
    }

    override fun validate0() {
        require(name.isNotEmpty()) { "Name must not be empty." }
        require(types.isNotEmpty()) { "Activity types must not be empty." }
        require(types.size <= MAX_TYPES) { "Activity can have a maximum of $MAX_TYPES types." }

        val desc = description
        require(!desc.isNullOrEmpty()) { "Description must not be empty." }
        require(desc.length <= 2500) { "Description must be at most 2500 characters." }
    }

    companion object {
        const val MAX_TYPES = 5
    }

}
