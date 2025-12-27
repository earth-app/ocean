package com.earthapp.ocean.boat

import com.earthapp.shovel.fetchDocument
import com.earthapp.shovel.querySelector
import com.earthapp.shovel.querySelectorAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

object IMEJ : Scraper() {

    override val name: String = "International Marine Energy Journal"
    override val baseUrl: String = "https://marineenergyjournal.org"
    override val tags: List<String> = listOf(
        "europe",
        "international",
        "maritime",
        "ocean",
        "education",
        "research",
        "energy",
        "renewable energy",
        "marine",
        "technology"
    )

    const val ITEM_COUNT = "div.cmp_pagination"
    const val SEARCH_RESULTS = "ul.search_results"

    override suspend fun search(query: String, pageLimit: Int): List<Page> {
        val url = "$baseUrl/imej/search/index?query=${query.replace(" ", "+")}"
        logger.debug { "Searching $name for query: '$query' - $url" }

        val firstPage = url.fetchDocument()
        val numOfItems = firstPage.querySelector("div.cmp_pagination")?.ownTextContent?.split("\\s+".toRegex())?.getOrNull(4)?.toIntOrNull() ?: 0
        logger.debug { "$name -- Found $numOfItems items." }

        val pages = if (numOfItems == 0) 0 else (numOfItems + 24) / 25

        val articles = mutableListOf<Page>()

        coroutineScope {
            for (i in 1..(if (pageLimit == -1) pages else minOf(pages, pageLimit))) {
                launch {
                    val document = if (i == 1) firstPage else "$url&searchPage=$i".fetchDocument()
                    logger.debug { "$name -- Searching through Page $i... (${document.url})" }

                    val articleUrls = document.querySelectorAll("$SEARCH_RESULTS > li > div.obj_article_summary > h3.title > a")
                        .mapNotNull { normalizeLink(baseUrl, it["href"]) }

                    logger.debug { "$name --- Found ${articleUrls.size} articles on Page $i" }

                    for (url in articleUrls) {
                        launch {
                            val articleDoc = url.fetchDocument()
                            val content = articleDoc.querySelector("section.item.abstract > p")?.textContent ?: ""

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