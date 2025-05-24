package com.earthapp.event

import com.earthapp.Exportable
import com.earthapp.util.ID_CHARACTERS
import com.earthapp.util.ID_LENGTH
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
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
        val event = Event {
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

}