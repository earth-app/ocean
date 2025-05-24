package com.earthapp.account

import com.earthapp.Exportable
import com.earthapp.util.ID_CHARACTERS
import com.earthapp.util.ID_LENGTH
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
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

        // To-From Binary Encrypted
        val (encryptedData, key) = account.toBinaryEncrypted()
        logger.debug { "Test Binary Encrypted: Data ${encryptedData.size}, Key ${key.size}}" }
        assertTrue { encryptedData.isNotEmpty() }
        assertTrue { key.isNotEmpty() }
        assertTrue { key.size == Exportable.CIPHER_SIZE / 4 }

        val deserializedEncrypted = Exportable.fromBinaryEncrypted(encryptedData, key) as? Account
        assertNotNull(deserializedEncrypted)
        deserializedEncrypted.validate()
        assertEquals(account, deserializedEncrypted)
    }

}