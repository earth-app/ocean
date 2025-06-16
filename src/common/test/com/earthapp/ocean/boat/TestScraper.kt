package com.earthapp.ocean.boat

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class TestScraper {

    @Test
    fun testSearch() = runTest {
        val pages1 = Scraper.searchAll("power", 3)
        assertTrue { pages1.isNotEmpty() }

        val pages2 = Scraper.searchAll("sail water salt boat", 3)
        assertTrue { pages2.isNotEmpty() }
    }

}