package com.earthapp.activity

import com.earthapp.Exportable
import com.earthapp.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
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
    var description: String? = null

    /**
     * The type of the activity.
     */
    val types = mutableListOf<ActivityType>()

    internal constructor(name: String, description: String? = null, vararg types: ActivityType) : this(name.lowercase(), name) {
        this.description = description
        this.types.addAll(types)
    }

    override fun validate0() {
        require(id.isNotEmpty()) { "ID must not be empty." }
        require(name.isNotEmpty()) { "Name must not be empty." }
        require(types.isNotEmpty()) { "Activity types must not be empty." }
        require(types.size >= 3) { "Activity can have a maximum of 3 types." }

        require(!description.isNullOrEmpty()) { "Description must not be empty." }
        require(description!!.length in 0..2500) { "Description must be between 0 and 2500 characters." }
    }

    companion object {

        /**
         * Creates an Activity instance from a JSON string.
         * This method is used to deserialize an activity from the JSON representation returned by Cloud.
         * @param jsonString The JSON string representing the activity.
         * @return An Activity instance populated with the data from the JSON string.
         */
        fun fromCloudJson(jsonString: String): Activity {
            val obj = json.decodeFromString<JsonObject>(jsonString)

            val id = obj["name"]?.jsonPrimitive?.contentOrNull ?: throw IllegalArgumentException("ID is required")
            val name = obj["human_name"]?.jsonPrimitive?.contentOrNull ?: throw IllegalArgumentException("Name is required")
            val description = obj["description"]?.jsonPrimitive?.contentOrNull ?: "No description provided."
            val types = obj["types"]?.jsonPrimitive?.contentOrNull?.split(",")?.map { ActivityType.valueOf(it) }
                ?: throw IllegalArgumentException("Types are required")

            val activity = Activity(id, name)
            activity.description = description
            activity.types.addAll(types)

            activity.validate()
            return activity
        }

    }

}