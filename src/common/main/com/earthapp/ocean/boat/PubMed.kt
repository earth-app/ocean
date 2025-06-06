package com.earthapp.ocean.boat

import com.earthapp.shovel.fetchDocument
import com.earthapp.shovel.getTitle
import com.earthapp.shovel.querySelector
import com.earthapp.shovel.querySelectorAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class PubMed : Scraper() {

    override val name: String = "PubMed"
    override val baseUrl: String = "https://pubmed.ncbi.nlm.nih.gov"
    override val tags: List<String> = listOf("medical", "research")

    val pageCount = "div.top-pagination > div.page-number-wrapper > label.of-total-pages"
    val searchResults = "div.search-results-chunk"
    val articleTextQueries = listOf(
        "div#abstract > div.abstract-content > p",
        "p#copyright",
        "div#conflict-of-interest > div.statement > p"
    )
    val doiLink = "span.doi > a.id-link"

    override suspend fun search(query: String): List<Page> {
        val url = "$baseUrl/?term=${query.replace(" ", "+")}&filter=simsearch2.ffrft&sort=date"
        val firstPage = url.fetchDocument()
        val pages = (firstPage.querySelector(pageCount)?.textContent ?: "of 1").split("\\s+".toRegex())[1].toIntOrNull() ?: 1

        val articles = mutableListOf<Page>()

        coroutineScope {
            for (i in 1..pages) {
                if (i == 1) continue // skip the first page as it is already fetched
                launch {
                    val document = "$url&page=$i".fetchDocument()
                    val articleChunks = document.querySelectorAll("$searchResults > article.full-docsum")

                    for (article in articleChunks) {
                        launch {
                            val pos = article["data-rel-pos"] ?: "1"
                            val url = document.querySelector("article[data-rel-pos='$pos'] > div.docsum-wrap > div.docsum-content > a.docsum-title")?.get("href")
                            if (url == null) return@launch

                            val href = "$baseUrl$url"
                            val articleDoc = href.fetchDocument()

                            val title = articleDoc.getTitle() ?: "Unnamed Article - $name"
                            val authors = articleDoc.querySelector("div.authors-list")
                            var author = authors?.children?.get(0)?.textContent ?: "Anonymous"
                            if ((authors?.children?.size ?: 0) > 1) author += " et al."

                            var content = ""
                            for (query in articleTextQueries) {
                                val elements = articleDoc.querySelectorAll(query)
                                if (elements.isNotEmpty()) content += elements.joinToString("\n") { it.textContent }
                            }
                            if (content.isEmpty()) content = "No content available."

                            val links = articleDoc.querySelector(doiLink)?.get("href")?.let { mapOf("DOI" to it) } ?: emptyMap()

                            articles.add(
                                Page(
                                    url = href,
                                    title = title,
                                    author = author,
                                    content = content,
                                    source = name,
                                    links = links
                                )
                            )
                        }
                    }
                }
            }
        }

        return articles
    }

}