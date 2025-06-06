@file:OptIn(ExperimentalJsExport::class)

package com.earthapp.ocean.boat

import com.earthapp.Exportable
import kotlinx.serialization.Serializable
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * Represents a base class for scrapers in the Earth App.
 */
@JsExport
abstract class Scraper {

    /**
     * The name of the scraper.
     */
    abstract val name: String

    /**
     * The base URL of the scraper.
     */
    abstract val baseUrl: String

    /**
     * The tags associated with the scraper.
     */
    abstract val tags: List<String>

    /**
     * Searches for pages based on the provided query.
     */
    @JsExport.Ignore
    abstract suspend fun search(query: String): List<Page>

    init {
        registeredScrapers.add(this)
    }

    /**
     * Represents a page scraped by the scraper.
     */
    @Serializable
    data class Page(
        /**
         * The URL of the page.
         */
        val url: String,
        /**
         * The title of the page.
         */
        val title: String,
        /**
         * The author of the page.
         */
        val author: String,
        /**
         * Some string content of the page.
         */
        val content: String,
        /**
         * The source or publisher of the page.
         */
        val source: String = "",
        /**
         * Additional links associated with the page, represented as a map of link names to URLs.
         */
        val links: Map<String, String> = emptyMap(),
        /**
         * The image associated with the page, represented as a byte array.
         */
        val image: ByteArray = byteArrayOf()
    ) : Exportable() {
        override val id: String
            get() = url

        override fun validate0() {
            require(url.isNotEmpty()) { "URL must not be empty." }
            require(title.isNotEmpty()) { "Title must not be empty." }
            require(content.isNotEmpty()) { "Content must not be empty." }
            require(author.isNotEmpty()) { "Author must not be empty." }
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Page

            return url == other.url
        }

        override fun hashCode(): Int = url.hashCode()
    }

    companion object {
        internal val registeredScrapers: MutableList<Scraper> = mutableListOf()
    }

}