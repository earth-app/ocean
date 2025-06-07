package com.earthapp.ocean.boat

import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class TestScraper {

    @Test
    fun testSearch() = runTest {
        for (scraper in Scraper.registeredScrapers)
            launch {
                val pages = scraper.search("power", 3)
                assertTrue { pages.isNotEmpty() }
            }
    }

}