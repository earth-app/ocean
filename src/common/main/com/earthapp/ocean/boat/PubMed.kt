package com.earthapp.ocean.boat

import com.earthapp.shovel.Element
import com.earthapp.shovel.fetchDocument
import com.earthapp.shovel.querySelector
import com.earthapp.shovel.querySelectorAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object PubMed : Scraper() {

    override val name: String = "PubMed"
    override val baseUrl: String = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils"
    override val tags: List<String> = listOf(
        "usa", "medical", "research", "education", "science",
        "health", "biomedical", "biology", "ncbi", "eutils"
    )

    const val PER_PAGE = 250

    override suspend fun search(query: String, pageLimit: Int): List<Page> {
        val delayMs = if (isAuthenticated(name)) 100L else 400L
        val articles = mutableListOf<Page>()

        try {
            val searchResult = performESearch(query)

            if (searchResult.webEnv.isEmpty() || searchResult.queryKey.isEmpty()) {
                logger.warn { "Failed to get WebEnv or QueryKey from ESearch" }
                return emptyList()
            }

            logger.info { "Found ${searchResult.count} articles for query: $query; Taking ${pageLimit * PER_PAGE}" }

            coroutineScope {
                val totalPages = if (pageLimit == -1) (searchResult.count + 99) / PER_PAGE else minOf((searchResult.count + 99) / PER_PAGE, pageLimit)
                for (page in 0 until totalPages) {
                    delay(delayMs) // Throttle requests to avoid hitting rate limits (unauthenticated: 3/second, authenticated: 10/second)
                    launch {
                        val offset = page * PER_PAGE
                        val articlesPage = performEFetch(searchResult.webEnv, searchResult.queryKey, offset = offset)
                        articles.addAll(articlesPage)
                        logger.debug { "Fetched page ${page + 1} of articles ($PER_PAGE articles on page)." }
                    }
                }
            }
        } catch (e: Exception) {
            logger.error { "Error searching PubMed E-utilities: ${e.message}" }
        }

        return articles
    }

    override fun String.addApiKey(): String {
        val apiKey = getApiKey(this@PubMed)
        if (apiKey.isEmpty()) return this
        return "$this&api_key=$apiKey"
    }

    private data class ESearchResult(
        val webEnv: String,
        val queryKey: String,
        val count: Int
    )

    private suspend fun performESearch(query: String): ESearchResult {
        val encodedQuery = query.replace(" ", "+")
        val url = "$baseUrl/esearch.fcgi?db=pubmed&term=$encodedQuery&usehistory=y"

        logger.debug { "ESearch URL: $url" }

        val doc = url.addApiKey().fetchDocument()

        val webEnv = doc.querySelector("WebEnv")?.textContent ?: ""
        val queryKey = doc.querySelector("QueryKey")?.textContent ?: ""
        val count = doc.querySelector("Count")?.textContent?.toIntOrNull() ?: 0

        return ESearchResult(webEnv, queryKey, count)
    }

    private suspend fun performEFetch(webEnv: String, queryKey: String, limit: Int = PER_PAGE, offset: Int = 0): List<Page> {
        val url = "$baseUrl/efetch.fcgi?db=pubmed&query_key=$queryKey&WebEnv=$webEnv&rettype=abstract&retmode=xml&retmax=$limit&retstart=$offset"

        logger.debug { "EFetch URL: $url" }

        val doc = url.addApiKey().fetchDocument()
        val articles = mutableListOf<Page>()

        coroutineScope {
            doc.querySelectorAll("PubmedArticle").forEach { article ->
                launch {
                    try {
                        val page = parseArticle(article)
                        if (page != null) {
                            articles.add(page)
                        }
                    } catch (e: Exception) {
                        logger.error { "Error parsing article: ${e.message}" }
                    }
                }
            }
        }

        return articles
    }

    private fun parseArticle(article: Element): Page? {
        val pmid = article.querySelector("PMID")?.textContent ?: return null
        val pubmedUrl = "https://pubmed.ncbi.nlm.nih.gov/$pmid/"

        logger.debug { "$name --- Processing article: $pubmedUrl" }

        val titleElement = article.querySelector("ArticleTitle")
        val title = titleElement?.textContent ?: "Unknown Title"

        val authorElements = article.querySelectorAll("Author")
        val authors = authorElements.mapNotNull { author ->
            val lastName = author.querySelector("LastName")?.textContent
            val foreName = author.querySelector("ForeName")?.textContent
            when {
                lastName != null && foreName != null -> "$foreName $lastName"
                lastName != null -> lastName
                else -> null
            }
        }

        val author = formatAuthors(authors)

        // Parse journal info
        val journal = article.querySelector("Title")?.textContent ?: "Unknown Journal"
        val pubDate = article.querySelector("PubDate Year")?.textContent ?:
        article.querySelector("ArticleDate Year")?.textContent ?: "Unknown Date"

        // Parse abstract
        val abstractTexts = article.querySelectorAll("AbstractText")
        val abstract = abstractTexts.joinToString("\n") { it.textContent }

        // Parse DOI
        val doi = article.querySelectorAll("ArticleId").find {
            it["IdType"] == "doi"
        }?.textContent

        val links = mutableMapOf<String, String>()
        if (!doi.isNullOrEmpty()) {
            links["DOI"] = "https://doi.org/$doi"
        }
        links["PubMed"] = pubmedUrl

        return Page(
            url = pubmedUrl,
            title = title,
            author = author,
            source = journal,
            date = pubDate,
            links = links,
            faviconUrl = "https://pubmed.ncbi.nlm.nih.gov/favicon.ico"
        ).apply {
            this.abstract = abstract
            content = abstract
            themeColor = "#326295"
            keywords.addAll(listOf("biomedical", "research", "pubmed", "ncbi"))

            if (journal.isNotEmpty()) {
                keywords.add(journal.lowercase())
            }

            validate()
        }
    }
}