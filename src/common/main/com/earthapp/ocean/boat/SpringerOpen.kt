package com.earthapp.ocean.boat

import com.earthapp.shovel.fetchDocument
import com.earthapp.shovel.querySelector
import com.earthapp.shovel.querySelectorAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

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
                val maxPageConcurrent = 4
                val pageJobs = mutableListOf<kotlinx.coroutines.Job>()

                for (i in 1..totalPages) {
                    // Wait for slot when at max concurrent
                    while (pageJobs.size >= maxPageConcurrent) {
                        delay(5L)
                        pageJobs.removeAll { it.isCompleted }
                    }

                    val pageJob = launch {
                        try {
                            delay((i % 3) * 50L) // Minimal stagger to avoid thundering herd
                            val document = if (i == 1) firstPage else "$url&page=$i".fetchDocument()
                            logger.debug { "$name -- Searching through Page $i... (${document.url})" }

                            val articleUrls = document.querySelectorAll(ARTICLE_URLS)
                                .mapNotNull { normalizeLink(baseUrl, it["href"]) }
                                .filter { it.contains("springeropen.com") }

                            logger.debug { "$name --- Found ${articleUrls.size} articles on Page $i" }

                            val maxArticleConcurrent = 6
                            val articleJobs = mutableListOf<kotlinx.coroutines.Job>()

                            for (articleUrl in articleUrls) {
                                // Wait for slot when at max concurrent
                                while (articleJobs.size >= maxArticleConcurrent) {
                                    delay(5L)
                                    articleJobs.removeAll { it.isCompleted }
                                }

                                val articleJob = launch {
                                    try {
                                        logger.debug { "$name --- Processing article on Page $i: $articleUrl" }

                                        val articleDoc = articleUrl.fetchDocument()
                                        val contents = articleDoc.querySelectorAll("main > article > section")
                                            .map { it.textContent }

                                        val content = contents.joinToString("\n\n")
                                        if (content.length < MIN_CONTENT_SIZE) {
                                            logger.warn { "$name --- Skipping article at $articleUrl due to insufficient content length." }
                                            return@launch
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
                                articleJobs.add(articleJob)
                            }

                            // Wait for all articles on this page
                            articleJobs.joinAll()
                        } catch (e: Exception) {
                            logger.error { "Error fetching page $i: ${e.message}" }
                        }
                    }
                    pageJobs.add(pageJob)
                }

                // Wait for remaining page jobs
                pageJobs.joinAll()
            }
        } catch (e: Exception) {
            logger.error { "Error searching $name: ${e.message}" }
        }

        return articles
    }

}

