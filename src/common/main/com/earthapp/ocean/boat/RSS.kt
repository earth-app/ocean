@file:OptIn(ExperimentalJsExport::class)

package com.earthapp.ocean.boat

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * Represents a collection of RSS feeds.
 */
@JsExport
class RSS(
    /**
     * The name of the RSS feed.
     */
    name: String,
    /**
     * The base URL of the RSS feed.
     */
    val url: String,
    /**
     * The tags associated with this RSS feed.
     */
    override val tags: List<String> = emptyList()
) : Scraper() {

    override val name: String = "RSS Feed [$name]"
    override val baseUrl: String = url

    override suspend fun search(query: String, pageLimit: Int): List<Page> {
        val queries = query.split(" ").filter { it.isNotBlank() }
        return retrieveRSSFeed(url).filter { page ->
            queries.all { query ->
                page.title.contains(query, ignoreCase = true) ||
                        page.abstract.contains(query, ignoreCase = true) ||
                        page.content.contains(query, ignoreCase = true)
            }
        }.take(pageLimit)
    }

}

expect suspend fun retrieveRSSFeed(url: String): List<Scraper.Page>