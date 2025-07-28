package com.earthapp.activity

import com.earthapp.Exportable
import com.earthapp.util.ID_CHARACTERS
import com.earthapp.util.ID_LENGTH
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TestActivity {

    private val logger = KotlinLogging.logger("com.earthapp.activity.TestActivity")

    @Test
    fun testSerialize() = runTest {
        val activity = Activity("test-activity-id", "Test Activity").apply {
            description = "A test activity"
            types.add(ActivityType.OTHER)
            aliases.add("TA")
        }

        // To-From JSON
        val json = activity.toJson()
        logger.debug { "Test JSON: $json" }
        assertTrue { json.isNotEmpty() }

        val deserializedJson = Exportable.fromJson(json) as? Activity
        assertNotNull(deserializedJson)
        deserializedJson.validate()
        assertEquals(activity.id, deserializedJson.id)
        assertEquals(activity.name, deserializedJson.name)
        assertEquals(activity.description, deserializedJson.description)
        assertEquals(activity.types, deserializedJson.types)
        assertEquals(activity.aliases, deserializedJson.aliases)

        // To-From Binary
        val binary = activity.toBinary()
        logger.debug { "Test Binary: Size ${binary.size}" }
        assertTrue { binary.isNotEmpty() }

        val deserializedBinary = Exportable.fromBinary(binary) as? Activity
        assertNotNull(deserializedBinary)
        deserializedBinary.validate()
        assertEquals(activity.id, deserializedBinary.id)
        assertEquals(activity.name, deserializedBinary.name)
        assertEquals(activity.description, deserializedBinary.description)
        assertEquals(activity.types, deserializedBinary.types)
        assertEquals(activity.aliases, deserializedBinary.aliases)
    }

    @Test
    fun testDeserializePartial() = runTest {
        val json = """
            {
                "type": "com.earthapp.activity.Activity",
                "id": "partial_activity",
                "name": "Partial Activity"
            }
        """.trimIndent()

        val activity = Exportable.fromJson(json) as? Activity
        assertNotNull(activity)
        logger.debug { "Deserialized Activity: ${activity.toJson()}" }

        assertFalse { activity.toJson().isEmpty() }

        // Should throw on validate() because required fields are missing
        try {
            activity.validate()
            assertFalse(true, "Validation should fail due to missing required fields")
        } catch (e: IllegalArgumentException) {
            // Expected
        }

        assertEquals("partial_activity", activity.id)
        assertEquals("Partial Activity", activity.name)
    }

    @Test
    fun validateTypesLimit() = runTest {
        val activity = Activity("test-id", "Test Activity")
        repeat(Activity.MAX_TYPES) {
            activity.types.add(ActivityType.OTHER)
        }
        try {
            activity.types.add(ActivityType.OTHER)
            activity.validate()
            assertFalse(true, "Should not allow more than MAX_TYPES")
        } catch (e: IllegalArgumentException) {
            // Expected
        }
    }
}