package com.earthapp.ocean.boat

import com.earthapp.shovel.fetchText
import io.ktor.http.encodeURLPathPart
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object DOAJ : Scraper() {

    override val name: String = "DOAJ"
    override val baseUrl: String = "https://doaj.org/api/v4"
    override val tags: List<String> = listOf(
        "open-access", "academic", "research", "journals", "articles",
        "scientific", "scholarly", "publishing", "global"
    )

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Serializable
    private data class DOAJSearchResponse(
        val total: Int,
        val page: Int,
        val pageSize: Int,
        val results: List<DOAJArticle>,
        val next: String? = null
    )

    @Serializable
    private data class DOAJArticle(
        val id: String,
        @SerialName("created_date")
        val createdDate: String,
        @SerialName("last_updated")
        val lastUpdated: String,
        val bibjson: BibJsonData
    )

    @Serializable
    private data class BibJsonData(
        val title: String,
        val abstract: String? = null,
        val author: List<AuthorData> = emptyList(),
        val journal: JournalData,
        val year: String? = null,
        val month: String? = null,
        val identifier: List<IdentifierData> = emptyList(),
        val link: List<LinkData> = emptyList(),
        val keywords: List<String> = emptyList(),
        val subject: List<SubjectData> = emptyList(),
        @SerialName("start_page")
        val startPage: String? = null,
        @SerialName("end_page")
        val endPage: String? = null
    )

    @Serializable
    private data class AuthorData(
        val name: String,
        val affiliation: String? = null
    )

    @Serializable
    private data class JournalData(
        val title: String,
        val volume: String? = null,
        val number: String? = null,
        val publisher: String? = null,
        val country: String? = null,
        val issns: List<String> = emptyList()
    )

    @Serializable
    private data class IdentifierData(
        val id: String,
        val type: String
    )

    @Serializable
    private data class LinkData(
        val url: String,
        val type: String
    )

    @Serializable
    private data class SubjectData(
        val term: String,
        val scheme: String? = null,
        val code: String? = null
    )

    override suspend fun search(query: String, pageLimit: Int): List<Page> {
        val delayMs = 200L
        val articles = mutableListOf<Page>()

        try {
            val searchUrl = "$baseUrl/search/articles/${query.encodeURLPathPart()}"

            logger.info { "Searching DOAJ with query: $query" }

            val initialResponse = performSearch(searchUrl, page = 1, pageSize = 1)
            val totalArticles = initialResponse.total

            logger.info { "Found $totalArticles articles for query: $query" }

            if (totalArticles == 0) {
                return emptyList()
            }

            coroutineScope {
                val totalPages = if (pageLimit == -1) {
                    (totalArticles + (PER_PAGE - 1)) / PER_PAGE
                } else {
                    minOf((totalArticles + (PER_PAGE - 1)) / PER_PAGE, pageLimit)
                }

                for (page in 1..totalPages) {
                    delay(delayMs) // Rate limiting
                    launch {
                        try {
                            val response = performSearch(searchUrl, page = page, pageSize = PER_PAGE)
                            val pageArticles = response.results.mapNotNull { parseArticle(it) }
                            articles.addAll(pageArticles)
                            logger.debug { "Fetched page $page of articles (${pageArticles.size} articles on page)." }
                        } catch (e: Exception) {
                            logger.error { "Error fetching page $page: ${e.message}" }
                        }
                    }
                }
            }

        } catch (e: Exception) {
            logger.error { "Error searching DOAJ: ${e.message}" }
        }

        return articles
    }

    private suspend fun performSearch(baseUrl: String, page: Int, pageSize: Int): DOAJSearchResponse {
        val url = "$baseUrl?page=$page&pageSize=$pageSize"
        logger.debug { "DOAJ Search URL: $url" }

        val responseText = url.fetchText()
        return json.decodeFromString<DOAJSearchResponse>(responseText)
    }

    private fun parseArticle(article: DOAJArticle): Page? {
        try {
            val bibjson = article.bibjson

            if (bibjson.title.isEmpty()) {
                logger.warn { "Skipping article with empty title: ${article.id}" }
                return null
            }

            val abstract = bibjson.abstract
            if (abstract == null) {
                logger.warn { "Skipping article with no abstract: ${article.id}" }
                return null
            }

            if (abstract.length < MIN_CONTENT_SIZE) {
                logger.warn { "Skipping article with too short abstract: ${article.id}" }
                return null
            }

            val articleUrl = bibjson.link.find { it.type == "fulltext" }?.url
                ?: "https://doaj.org/article/${article.id}"

            logger.debug { "$name --- Processing article: $articleUrl" }

            val authors = bibjson.author.map { it.name }
            val author = formatAuthors(authors)

            val journal = bibjson.journal.title
            val volume = bibjson.journal.volume?.let { "Vol. $it" }
            val issue = bibjson.journal.number?.let { "Issue $it" }
            val publisher = bibjson.journal.publisher

            val sourceComponents = listOfNotNull(volume, issue, journal, publisher)
            val source = sourceComponents.joinToString(", ").takeIf { it.isNotEmpty() } ?: "Unknown Journal"

            // Format date
            val year = bibjson.year ?: ""
            val month = bibjson.month?.let {
                when (it.padStart(2, '0')) {
                    "01" -> "January"
                    "02" -> "February"
                    "03" -> "March"
                    "04" -> "April"
                    "05" -> "May"
                    "06" -> "June"
                    "07" -> "July"
                    "08" -> "August"
                    "09" -> "September"
                    "10" -> "October"
                    "11" -> "November"
                    "12" -> "December"
                    else -> it
                }
            }
            val date = listOfNotNull(month, year).joinToString(" ").takeIf { it.isNotEmpty() } ?: "Unknown Date"

            // Build links
            val links = mutableMapOf<String, String>()

            // Add DOI link
            bibjson.identifier.find { it.type.equals("DOI", ignoreCase = true) }?.let { doi ->
                links["DOI"] = if (doi.id.startsWith("http")) doi.id else "https://doi.org/${doi.id}"
            }

            // Add fulltext link
            bibjson.link.find { it.type == "fulltext" }?.let { link ->
                links["Full Text"] = link.url
            }

            // Add DOAJ link
            links["DOAJ"] = "https://doaj.org/article/${article.id}"

            // Add ISSN links
            bibjson.journal.issns.firstOrNull()?.let { issn ->
                links["ISSN"] = "https://portal.issn.org/resource/ISSN/$issn"
            }

            // Build page info
            val pageInfo = when {
                bibjson.startPage != null && bibjson.endPage != null ->
                    "pp. ${bibjson.startPage}-${bibjson.endPage}"
                bibjson.startPage != null -> "p. ${bibjson.startPage}"
                else -> null
            }

            return Page(
                url = articleUrl,
                title = bibjson.title,
                author = author,
                source = source + if (pageInfo != null) ", $pageInfo" else "",
                date = date,
                links = links,
                faviconUrl = "https://doaj.org/assets/img/favicon/favicon.ico"
            ).apply {
                this.abstract = abstract
                this.content = abstract

                themeColor = "#f1645a" // DOAJ brand color

                keywords.addAll(bibjson.keywords)
                keywords.addAll(bibjson.subject.map { it.term })
                keywords.addAll(listOf("open-access", "doaj", "academic", "research"))

                if (journal.isNotEmpty()) {
                    keywords.add(journal.lowercase())
                }

                bibjson.journal.country?.let { country ->
                    keywords.add(country.lowercase())
                }

                validate()
            }

        } catch (e: Exception) {
            logger.error { "Error parsing DOAJ article ${article.id}: ${e.message}" }
            return null
        }
    }
}