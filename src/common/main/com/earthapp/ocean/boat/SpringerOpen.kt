package com.earthapp.ocean.boat

import com.earthapp.shovel.fetchDocument
import com.earthapp.shovel.querySelector
import com.earthapp.shovel.querySelectorAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

object SpringerOpen : Scraper() {

    override val name: String = "SpringerOpen"
    override val baseUrl: String = "https://www.springeropen.com"
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

    const val PAGE_COUNT = "main > div[data-test='search-content'] > div.c-divider > div > p"
    const val ARTICLE_URLS = "main > div[data-test='search-content'] > ol.c-listing > li > article > h3[itemprop='name'] > a[itemprop='url']"

    override suspend fun search(query: String, pageLimit: Int): List<Page> {
        val url = "$baseUrl/search?query=${query.replace(" ", "+")}&sort=PubDate&searchType=publisherSearch"
        logger.debug { "Searching $name for query: '$query'" }

        val firstPage = url.fetchDocument()
        val pages = (firstPage.querySelector(PAGE_COUNT)?.textContent?.replace(",", "") ?: "Page 1 of 1").split("\\s+".toRegex())[3].toIntOrNull() ?: 1

        val articles = mutableListOf<Page>()

        coroutineScope {
            for (i in 1..(if (pageLimit == -1) pages else minOf(pages, pageLimit))) {
                launch {
                    logger.debug { "$name -- Searching through Page $i..." }

                    val document = if (i == 1) firstPage else "$url&page=$i".fetchDocument()
                    val articleUrls = document.querySelectorAll(ARTICLE_URLS)
                        .mapNotNull { normalizeLink(baseUrl, it["href"]) }
                        .filter { it.contains("springeropen.com") }

                    for (url in articleUrls) {
                        launch {
                            logger.debug { "$name --- Processing article on Page $i: $url" }

                            val articleDoc = url.fetchDocument()
                            val contents = articleDoc.querySelectorAll("main > article > section")
                                .map { it.textContent }

                            articles.add(
                                createPage(url, articleDoc) {
                                    content = contents.joinToString("\n\n")
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

