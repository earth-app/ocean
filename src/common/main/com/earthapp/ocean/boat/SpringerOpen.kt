package com.earthapp.ocean.boat

import com.earthapp.shovel.fetchDocument
import com.earthapp.shovel.querySelector
import com.earthapp.shovel.querySelectorAll
import kotlinx.coroutines.coroutineScope
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

        coroutineScope {
            for (i in 1..(if (pageLimit == -1) pages else minOf(pages, pageLimit))) {
                launch {
                    val document = if (i == 1) firstPage else "$url&page=$i".fetchDocument()
                    logger.debug { "$name -- Searching through Page $i... (${document.url})" }

                    val articleUrls = document.querySelectorAll(ARTICLE_URLS)
                        .mapNotNull { normalizeLink(baseUrl, it["href"]) }
                        .filter { it.contains("springeropen.com") }

                    logger.debug { "$name --- Found ${articleUrls.size} articles on Page $i" }

                    for (url in articleUrls) {
                        launch {
                            logger.debug { "$name --- Processing article on Page $i: $url" }

                            val articleDoc = url.fetchDocument()
                            val contents = articleDoc.querySelectorAll("main > article > section")
                                .map { it.textContent }

                            val content = contents.joinToString("\n\n")
                            if (content.length < MIN_CONTENT_SIZE) {
                                logger.warn { "$name --- Skipping article at $url due to insufficient content length." }
                                return@launch
                            }

                            articles.add(
                                createArticle(url, articleDoc) {
                                    this.content = content
                                }
                            )
                        }
                    }
                }
            }
        }

        return articles
    }

}

