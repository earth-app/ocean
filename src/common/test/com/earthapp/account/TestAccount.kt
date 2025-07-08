package com.earthapp.account

import com.earthapp.Exportable
import com.earthapp.Visibility
import com.earthapp.util.ID_CHARACTERS
import com.earthapp.util.ID_LENGTH
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TestAccount {

    private val logger = KotlinLogging.logger("com.earthapp.account.TestAccount")

    @Test
    fun testNewId() {
        val id = Account.newId()
        logger.debug { "Test ID: $id" }

        assertTrue { id.isNotEmpty() }
        assertTrue { id.length == ID_LENGTH }
        assertTrue { id.all { it in ID_CHARACTERS } }
    }

    @Test
    fun testSerialize() = runTest {
        val account = Account("johndoe") {
            firstName = "John"
            lastName = "Doe"
            email = "user@example.com"
            country = "US"
        }

        // To-From JSON
        val json = account.toJson()
        logger.debug { "Test JSON: $json" }
        assertTrue { json.isNotEmpty() }

        val deserializedJson = Exportable.fromJson(json) as? Account
        assertNotNull(deserializedJson)
        deserializedJson.validate()
        assertEquals(account, deserializedJson)

        // To-From Binary
        val binary = account.toBinary()
        logger.debug { "Test Binary: Size ${binary.size}" }
        assertTrue { binary.isNotEmpty() }

        val deserializedBinary = Exportable.fromBinary(binary) as? Account
        assertNotNull(deserializedBinary)
        deserializedBinary.validate()
        assertEquals(account, deserializedBinary)
    }

    @Test
    fun testDeserializePartial() = runTest {
        val json = """
            {
                "type": "com.earthapp.account.Account",
                "id": "eKPC4fChej1B78Us02SD1U8W",
                "username": "johndoe",
                "firstName": "John",
                "lastName": "Doe"
            }
        """.trimIndent()

        val account = Exportable.fromJson(json) as? Account
        assertNotNull(account)
        logger.debug { "Deserialized Account: ${account.toJson()}" }

        assertFalse { account.toJson().isEmpty() }

        account.validate()

        assertEquals("eKPC4fChej1B78Us02SD1U8W", account.id)
        assertEquals("John", account.firstName)
        assertEquals("Doe", account.lastName)
        assertEquals("", account.email) // Default value
        assertEquals("", account.country) // Default value
        assertEquals(Visibility.UNLISTED, account.visibility) // Default value
    }

    @Test
    fun validatePrivacyFields() = runTest {
        val account = Account("johndoe") {
            firstName = "John"
            lastName = "Doe"
        }
        val valid = Account.getAllowedPrivacyFields()
        val keys = account.fieldPrivacy.keys

        for (field in keys)
            assertTrue { field in valid }
    }

}