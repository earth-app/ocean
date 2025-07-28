package com.earthapp.activity

import com.earthapp.Exportable
import com.earthapp.json
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.SerialName
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
    var name: String,
) : Exportable() {

    /**
     * The description of the activity.
     */
    var description: String? = null

    /**
     * A list of aliases for the activity.
     * Aliases are alternative names that can be used to refer to the activity.
     */
    val aliases = mutableListOf<String>()

    /**
     * The type of the activity.
     */
    @SerialName("activity_types")
    val types = mutableListOf<ActivityType>()

    internal constructor(name: String, description: String? = null, vararg types: ActivityType) : this(name.lowercase(), name) {
        this.description = description
        this.types.addAll(types)
    }

    /**
     * Patches the activity with new values.
     * @param name The new name of the activity.
     * @param description The new description of the activity.
     * @param types The new types of the activity.
     * @return The updated Activity instance.
     */
    fun patch(
        name: String = this.name,
        description: String? = this.description,
        types: List<ActivityType> = this.types,
        aliases: List<String> = this.aliases
    ): Activity {
        this.name = name
        this.description = description

        this.types.clear()
        this.types.addAll(types)

        this.aliases.clear()
        this.aliases.addAll(aliases)

        validate()
        return this
    }

    override fun validate0() {
        require(id.isNotEmpty()) { "ID must not be empty." }
        require(name.isNotEmpty()) { "Name must not be empty." }
        require(types.isNotEmpty()) { "Activity types must not be empty." }
        require(types.size <= MAX_TYPES) { "Activity can have a maximum of $MAX_TYPES types." }

        require(!description.isNullOrEmpty()) { "Description must not be empty." }
        require(description!!.length in 0..2500) { "Description must be between 0 and 2500 characters." }
    }

    /**
     * Adds a type to the activity.
     * @param type The type to add.
     * @return The updated Activity instance.
     */
    fun addType(type: ActivityType) {
        if (types.size >= MAX_TYPES) {
            logger.warn { "Cannot add type $type to activity '$id': maximum of $MAX_TYPES types reached." }
            return
        }

        types.add(type)
        validate()
    }

    /**
     * Adds multiple types to the activity.
     * @param types The types to add.
     * @return The updated Activity instance.
     */
    fun addTypes(vararg types: ActivityType) {
        if (this.types.size + types.size > MAX_TYPES) {
            logger.warn { "Cannot add types ${types.joinToString()} to activity '$id': maximum of $MAX_TYPES types reached." }
            return
        }

        this.types.addAll(types)
        validate()
    }

    /**
     * Removes a type from the activity.
     * @param type The type to remove.
     * @return The updated Activity instance.
     */
    fun removeType(type: ActivityType) {
        if (!types.contains(type)) {
            logger.warn { "Type $type is not present in the activity types for activity '$id'." }
            return
        }

        types.remove(type)
        validate()
    }

    /**
     * Removes multiple types from the activity.
     * @param types The types to remove.
     */
    fun removeTypes(vararg types: ActivityType) {
        for (type in types) {
            if (!this.types.contains(type)) {
                logger.warn { "Type $type is not present in the activity types for activity '$id'." }
                continue
            }
            this.types.remove(type)
        }
        validate()
    }

    /**
     * Checks if the activity matches a given name or any of its aliases.
     * @param name The name to check against the activity's name and aliases.
     * @return True if the activity's name or any of its aliases match the given name, false otherwise.
     */
    fun doesMatch(name: String): Boolean {
        return this.name.equals(name, ignoreCase = true) || aliases.any { it.equals(name, ignoreCase = true) }
    }

    companion object {
        private val logger = KotlinLogging.logger("com.earthapp.activity.Activity")

        const val MAX_TYPES = 5
    }

}