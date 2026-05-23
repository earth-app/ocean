package com.earthapp.ocean.boat

import com.earthapp.shovel.fetchDocument
import com.earthapp.shovel.querySelector
import com.earthapp.shovel.querySelectorAll
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

object SpringerOpen : Scraper() {

    override val name: String = "SpringerOpen"
    override val baseUrl: String = "https://link.springer.com"
    override val tags: List<String> = listOf(
        "open access",
        "research",
        "education",
        "science",
        "health",
        "biomedical",
        "biology",
        "humanities",
        "social sciences"
    )

    const val PAGE_COUNT = "span[data-test='results-data-total']"
    const val ARTICLE_URLS = "div.app-card-open__main > h3.app-card-open__heading > a.app-card-open__link"

    override suspend fun search(query: String, pageLimit: Int): List<Page> {
        val url = "$baseUrl/search?query=${query.replace(" ", "+")}&sortBy=newestFirst&openAccess=true&content-type=Article&date=m12"
        logger.debug { "Searching $name for query: '$query' - $url" }

        val firstPage = url.fetchDocument()
        val pageCount = firstPage.querySelector(PAGE_COUNT)?.textContent?.trim()?.replace(",", "") ?: "Showing 1-0 of 0 results"
        val pages = pageCount.substringAfter("of").substringBefore("results").trim().toIntOrNull()
            ?.let { (it + 19) / 20 } ?: 0
        val articles = mutableListOf<Page>()

        try {
            val totalPages = if (pageLimit == -1) pages else minOf(pages, pageLimit)

            coroutineScope {
                val pageGate = Semaphore(4)
                val articleGate = Semaphore(6)

                (1..totalPages).map { i ->
                    async {
                        pageGate.withPermit {
                            try {
                                delay((i % 3) * 50L)
                                val document = if (i == 1) firstPage else "$url&page=$i".fetchDocument()
                                logger.debug { "$name -- Searching through Page $i... (${document.url})" }

                                val articleUrls = document.querySelectorAll(ARTICLE_URLS)
                                    .mapNotNull { normalizeLink(baseUrl, it["href"]) }
                                    .filter { it.contains("springeropen.com") }

                                logger.debug { "$name --- Found ${articleUrls.size} articles on Page $i" }

                                coroutineScope {
                                    articleUrls.map { articleUrl ->
                                        async {
                                            articleGate.withPermit {
                                                try {
                                                    logger.debug { "$name --- Processing article on Page $i: $articleUrl" }

                                                    val articleDoc = articleUrl.fetchDocument()
                                                    val contents = articleDoc.querySelectorAll("main > article > section")
                                                        .map { it.textContent }

                                                    val content = contents.joinToString("\n\n")
                                                    if (content.length < MIN_CONTENT_SIZE) {
                                                        logger.warn { "$name --- Skipping article at $articleUrl due to insufficient content length." }
                                                        return@withPermit
                                                    }

                                                    articles.add(
                                                        createArticle(articleUrl, articleDoc) {
                                                            this.content = content
                                                        }
                                                    )
                                                } catch (e: Exception) {
                                                    logger.error { "Error processing article $articleUrl: ${e.message}" }
                                                }
                                            }
                                        }
                                    }.awaitAll()
                                }
                            } catch (e: Exception) {
                                logger.error { "Error fetching page $i: ${e.message}" }
                            }
                        }
                    }
                }.awaitAll()
            }
        } catch (e: Exception) {
            logger.error { "Error searching $name: ${e.message}" }
        }

        return articles
    }

}

