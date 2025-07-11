package com.earthapp.ocean.boat

import com.earthapp.util.getEnv
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class TestScraper {

    @Test
    fun testSearch() = runTest {
        Scraper.setApiKey(PubMed, getEnv("NCBI_API_KEY"))

        val pages1 = Scraper.searchAll("power", 3)
        println(pages1)
        assertTrue { pages1.isNotEmpty() }

        val pages2 = Scraper.searchAll("sail water salt boat", 3)
        assertTrue { pages2.isNotEmpty() }
    }

}