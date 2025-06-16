package com.earthapp.ocean.boat

import com.earthapp.shovel.fetchDocument
import com.earthapp.shovel.querySelector
import com.earthapp.shovel.querySelectorAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

object PubMed : Scraper() {

    override val name: String = "PubMed"
    override val baseUrl: String = "https://pubmed.ncbi.nlm.nih.gov"
    override val tags: List<String> = listOf(
        "usa",
        "medical",
        "research",
        "education",
        "science",
        "health",
        "biomedical",
        "biology"
    )

    const val PAGE_COUNT = "div.top-pagination > div.page-number-wrapper > label.of-total-pages"
    const val SEARCH_RESULTS = "div.search-results-chunk"
    val ARTICLE_TEXT_QUERIES = listOf(
        "div#abstract > div.abstract-content > p",
        "p#copyright",
        "div#conflict-of-interest > div.statement > p"
    )

    override suspend fun search(query: String, pageLimit: Int): List<Page> {
        val url = "$baseUrl/?term=${query.replace(" ", "+")}&filter=simsearch2.ffrft&sort=date"
        logger.debug { "Searching $name for query: '$query' - $url" }

        val firstPage = url.fetchDocument()
        val pages = (firstPage.querySelector(PAGE_COUNT)?.textContent?.replace(",", "") ?: "of 1").split("\\s+".toRegex())[1].toIntOrNull() ?: 1

        val articles = mutableListOf<Page>()

        coroutineScope {
            for (i in 1..(if (pageLimit == -1) pages else minOf(pages, pageLimit))) {
                launch {
                    val document = if (i == 1) firstPage else "$url&page=$i".fetchDocument()
                    logger.debug { "$name -- Searching through Page $i... (${document.url})" }

                    val articleChunks = document.querySelectorAll("$SEARCH_RESULTS > article.full-docsum")

                    logger.debug { "$name --- Found ${articleChunks.size} article chunks on Page $i" }

                    for (article in articleChunks) {
                        launch {
                            val pos = article["data-rel-pos"] ?: "1"
                            val url = document.querySelector("article[data-rel-pos='$pos'] > div.docsum-wrap > div.docsum-content > a.docsum-title")?.get("href")
                            val href = normalizeLink(baseUrl, url) ?: return@launch

                            logger.debug { "$name --- Processing article on Page $i: $url" }

                            val articleDoc = href.fetchDocument()

                            var contents = ""
                            for (query in ARTICLE_TEXT_QUERIES) {
                                val elements = articleDoc.querySelectorAll(query)
                                if (elements.isNotEmpty()) contents += elements.joinToString("\n") { it.textContent }
                            }

                            articles.add(
                                createPage(href, articleDoc) {
                                    content = contents
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