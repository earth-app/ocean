package com.earthapp.util

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.test.Test
import kotlin.test.assertTrue

class TestId {

    private val logger = KotlinLogging.logger("com.earthapp.util.TestId")

    @Test
    fun testNewId() {
        val id = newIdentifier()
        logger.debug { "Generated ID: $id" }

        assertTrue { id.isNotEmpty() }
        assertTrue { id.length == ID_LENGTH }
        assertTrue { id.all { it in ID_CHARACTERS } }
    }

    @Test
    fun testNewApiKey() {
        val apiKey = newApiKey()
        logger.debug { "Generated API Key: $apiKey" }

        assertTrue { apiKey.isNotEmpty() }
        assertTrue { apiKey.length == API_KEY_LENGTH }
        assertTrue { apiKey.startsWith("EA25K") }
        assertTrue { apiKey.endsWith("QL4DX") }
    }

}