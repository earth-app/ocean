package com.earthapp.event

import com.earthapp.Exportable
import com.earthapp.Visibility
import com.earthapp.account.Account
import com.earthapp.util.ID_CHARACTERS
import com.earthapp.util.ID_LENGTH
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.time.TimeSource

class TestEvent {

    private val logger = KotlinLogging.logger("com.earthapp.account.TestAccount")

    @Test
    fun testNewId() {
        val id = Event.newId()
        assertTrue { id.isNotEmpty() }
        assertTrue { id.length == ID_LENGTH }
        assertTrue { id.all { it in ID_CHARACTERS } }
    }

    @Test
    fun testSerialize() = runTest {
        val event = Event(Account.newId()) {
            name = "My Event"
            description = "This is a test event."
            date = TimeSource.Monotonic.markNow().elapsedNow().inWholeMilliseconds.toDouble()
        }

        // To-From JSON
        val json = event.toJson()
        logger.debug { "Test JSON: $json" }
        assertTrue { json.isNotEmpty() }

        val deserializedJson = Exportable.fromJson(json) as? Event
        assertNotNull(deserializedJson)
        deserializedJson.validate()
        assertEquals(event, deserializedJson)

        // To-From Binary
        val binary = event.toBinary()
        logger.debug { "Test Binary: Size ${binary.size}" }
        assertTrue { binary.isNotEmpty() }

        val deserializedBinary = Exportable.fromBinary(binary) as? Event
        assertNotNull(deserializedBinary)
        deserializedBinary.validate()
        assertEquals(event, deserializedBinary)
    }

    @Test
    fun testDeserializePartial() = runTest {
        val json = """
            {
                "type": "com.earthapp.event.Event",
                "id": "OSlw62766qdtyYDcbTQxqDgx",
                "hostId": "HZ6VRMhjHuNfgXb3XjQysOUd",
                "name": "Partial Event"
            }
        """.trimIndent()

        val event = Exportable.fromJson(json) as? Event
        assertNotNull(event)
        logger.debug { "Deserialized Event: ${event.toJson()}" }

        assertFalse { event.toJson().isEmpty() }

        event.validate()

        assertEquals("OSlw62766qdtyYDcbTQxqDgx", event.id)
        assertEquals("HZ6VRMhjHuNfgXb3XjQysOUd", event.hostId)
        assertEquals("Partial Event", event.name)
        assertEquals(0.0, event.date) // Default value
        assertEquals(0.0, event.endDate) // Default value
        assertEquals(null, event.location) // Default value
        assertEquals(EventType.IN_PERSON, event.type) // Default value
        assertEquals(Visibility.UNLISTED, event.visibility) // Default value
        assertEquals("", event.description) // Default value
    }
}