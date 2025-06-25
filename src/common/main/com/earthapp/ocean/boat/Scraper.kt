@file:OptIn(ExperimentalJsExport::class, ExperimentalJsStatic::class)

package com.earthapp.ocean.boat

import com.earthapp.CompressionSerializer
import com.earthapp.Exportable
import com.earthapp.shovel.Document
import com.earthapp.shovel.getFaviconUrl
import com.earthapp.shovel.getTitle
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.coroutineScope
import kotlinx.io.IOException
import kotlinx.serialization.Serializable
import kotlin.js.ExperimentalJsExport
import kotlin.js.ExperimentalJsStatic
import kotlin.js.JsExport
import kotlin.js.JsStatic

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
     * @param query The search query.
     * @param pageLimit The maximum number of pages to return. If -1, returns all pages.
     * @return A list of pages that match the search query.
     */
    @JsExport.Ignore
    abstract suspend fun search(query: String, pageLimit: Int = -1): List<Page>

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
         * The source or publisher of the page.
         */
        val source: String = "",
        /**
         * The date associated with the page, represented as a string.
         */
        val date: String = "",
        /**
         * Additional links associated with the page, represented as a map of link names to URLs.
         */
        val links: Map<String, String> = emptyMap(),
        /**
         * The URL of the favicon for the page.
         */
        val faviconUrl: String = "",
    ) : Exportable() {
        /**
         * The abstract of the page, which is a brief summary or description.
         * If the abstract is empty, it defaults to a placeholder message.
         */
        @Serializable(with = CompressionSerializer::class)
        var abstract: String = ""
            set(value) {
                field = if (value.isEmpty()) {
                    "No abstract available."
                } else {
                    value.trim()
                }
            }

        /**
         * Some string content of the page.
         */
        @Serializable(with = CompressionSerializer::class)
        var content: String = "No content available."
            set(value) {
                field = if (value.isEmpty()) {
                    "No content available."
                } else {
                    value.trim()
                }
            }

        /**
         * The theme color for the page, defaulting to white.
         */
        var themeColor: String = "#ffffff"

        /**
         * A list of keywords associated with the page.
         */
        val keywords: MutableList<String> = mutableListOf()

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

        override fun toString(): String {
            return "'$title' by $author on $date\n$url ($source)\n---\"$abstract\"\n-----\n$content\n-----\n${links.entries.joinToString(", ") { "${it.key}: ${it.value}" }}\n"
        }
    }

    companion object {
        internal val logger = KotlinLogging.logger("com.earthapp.ocean.boat.Scraper")

        internal val registeredScrapers = listOf<Scraper>(
            PubMed,
            IMEJ,
            SpringerOpen
        )

        /**
         * Searches for pages across all registered scrapers based on the provided query.
         * This function aggregates results from all scrapers and returns a distinct list of pages.
         * @param query The search query to use for finding pages.
         * @param pageLimit The maximum number of pages to return from each scraper. Defaults to 5.
         * @return A list of distinct pages that match the search query across all scrapers.
         */
        @JsExport.Ignore
        suspend fun searchAll(query: String, pageLimit: Int = 5): List<Page> {
            val articles = mutableListOf<Page>()

            coroutineScope {
                for (scraper in registeredScrapers) {
                    logger.info { "Searching in ${scraper.name} for query: $query" }

                    try {
                        articles.addAll(scraper.search(query, pageLimit))
                    } catch (e: IOException) {
                        logger.error { "Error while searching in ${scraper.name}: ${e.message}" }
                    }
                }
            }

            return articles.distinctBy { it.url }
        }

        // Utilities

        /**
         * Normalizes a link based on the base URL.
         * This function ensures that the link is in a valid format, converting it to HTTPS if necessary,
         * and resolving relative paths against the base URL.
         * @param baseUrl The base URL to resolve relative links against.
         * @param link The link to normalize.
         * @return A normalized link, or null if the input link is null or empty.
         */
        @Suppress("HttpUrlsUsage")
        @JsStatic
        fun normalizeLink(baseUrl: String, link: String?): String? {
            if (link == null || link.isEmpty()) return null

            if (link.startsWith("http://"))
                return link.replace("http://", "https://")

            if (link.startsWith("//"))
                return "https:$link"

            if (link.startsWith("/"))
                return if (baseUrl.endsWith("/") || link.startsWith('/')) {
                    "$baseUrl$link"
                } else {
                    "$baseUrl/$link"
                }

            return link
        }

        /**
         * Formats a list of authors into a readable string.
         * @param authors The list of authors.
         * @return A formatted string representing the authors.
         * If the list is empty, returns "Unknown Author".
         */
        @JsStatic
        fun formatAuthors(authors: List<String>): String {
            return when (authors.size) {
                0 -> "Unknown Author"
                1 -> authors[0]
                2 -> "${authors[0]} and ${authors[1]}"
                3 -> "${authors[0]}, ${authors[1]} and ${authors[2]}"
                else -> "${authors[0]}, ${authors[1]}, et. al"
            }
        }

        /**
         * Constructs a `Page` object from the provided URL and article document based on its metadata.
         * Content is not included in this method; it should be set separately.
         * @param href The URL of the article.
         * @param articleDoc The document representing the article.
         * @param apply A lambda function to apply additional properties to the `Page` object.
         * @return A `Page` object containing the article's metadata.
         */
        @JsStatic
        fun createArticle(href: String, articleDoc: Document, apply: Page.() -> Unit = {}): Page {
            val metadata = articleDoc.metadata
            val title = metadata["citation_title"]?.getOrNull(0) ?: articleDoc.getTitle() ?: "Unknown Title"

            val volume = metadata["citation_volume"]?.getOrNull(0)?.let { "Vol. $it, " } ?: ""
            val issue = metadata["citation_issue"]?.getOrNull(0)?.let { "Issue $it, " } ?: ""
            val journal = (metadata["citation_journal_title"]?.getOrNull(0) ?: metadata["citation_publisher"]?.getOrNull(0) ?: "Unknown Journal")
                .split(" ")
                .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
            val firstPage = metadata["citation_firstpage"]?.getOrNull(0)
            val lastPage = metadata["citation_lastpage"]?.getOrNull(0)
            val pp = when {
                firstPage != null && lastPage != null -> ", pp. $firstPage-$lastPage"
                firstPage != null -> ", p. $firstPage"
                lastPage != null -> ", p. $lastPage"
                else -> ""
            }

            val container = "$volume$issue$journal$pp".trim().takeIf { it.isNotEmpty() } ?: "Unknown Container"

            val author = formatAuthors(metadata["citation_author"] ?: emptyList())

            val date = metadata["citation_date"]?.getOrNull(0) ?:
                metadata["citation_online_date"]?.getOrNull(0) ?:
                metadata["citation_publication_date"]?.getOrNull(0) ?:
                "Unknown Date"

            val links = mutableMapOf<String, String>()
            metadata["citation_pdf_url"]?.getOrNull(0)?.let { links["PDF"] = it }
            metadata["citation_doi"]?.getOrNull(0)?.let { links["DOI"] = "https://doi.org/$it" }
            metadata["citation_issn"]?.getOrNull(0)?.let { links["ISSN"] = "https://portal.issn.org/resource/ISSN/$it" }

            return Page(
                url = href,
                title = title,
                author = author,
                source = container,
                date = date,
                links = links,
                faviconUrl = articleDoc.getFaviconUrl() ?: ""
            ).apply {
                abstract = metadata["citation_abstract"]?.getOrNull(0) ?: ""
                themeColor = metadata["theme_color"]?.getOrNull(0) ?: "#ffffff"

                keywords.addAll(metadata["citation_keywords"] ?: metadata["dc.subject"] ?: emptyList())
                keywords.addAll(metadata["citation_article_type"] ?: emptyList())
                keywords.addAll(metadata["og:type"] ?: emptyList())

                apply()
                validate()
            }
        }
    }

}