package com.earthapp.activity

import com.earthapp.Exportable
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

        val json = activity.toJson()
        logger.debug { "Test JSON: $json" }
        assertTrue { json.isNotEmpty() }

        val deserialized = Exportable.fromJson(json) as? Activity
        assertNotNull(deserialized)
        deserialized.validate()
        assertEquals(activity.id, deserialized.id)
        assertEquals(activity.name, deserialized.name)
        assertEquals(activity.description, deserialized.description)
        assertEquals(activity.types, deserialized.types)
        assertEquals(activity.aliases, deserialized.aliases)
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
