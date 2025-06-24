package com.earthapp.ocean.boat

import com.earthapp.util.stripHTML
import kotlinx.coroutines.await
import kotlin.js.Promise

actual suspend fun retrieveRSSFeed(url: String): List<Scraper.Page> {
    val parser = Parser()
    val channel = parser.parseURL(url).await()

    val home = channel.link ?: url.substringAfter("//").substringBefore('/').let { "https://$it" }
    val faviconUrl = url.substringAfter("//").substringBefore("/").let { domain ->
        "https://$domain/favicon.ico"
    }

    if (channel.items == null) return emptyList()

    val items = channel.items!!.toList()
    val channelTitle = channel.title ?: "RSS Feed"

    return items.mapNotNull { item ->
        val url = item.link ?: item.guid ?: return@mapNotNull null
        val title = item.title ?: return@mapNotNull null

        Scraper.Page(
            url = url,
            title = title,
            author = item.creator ?: channelTitle,
            source = channelTitle,
            date = item.isoDate ?: item.pubDate ?: "",
            links = mapOf("Home" to home),
            faviconUrl = faviconUrl
        ).apply {
            abstract = (item.description ?: item.summary)?.stripHTML() ?: "No summary available."
            content = (item.contentSnippet ?: item.content)?.stripHTML() ?: "No content available."
        }
    }
}

@JsModule("rss-parser")
@JsNonModule
external class Parser {
    fun parseURL(url: String): Promise<RSSChannel>
}

external interface RSSChannel {
    val title: String?
    val description: String?
    val link: String?
    val copyright: String?
    val items: Array<RSSItem>?
}

external interface RSSItem {
    val title: String?
    val link: String?
    val guid: String?
    val description: String?
    val creator: String?
    val content: String?
    val contentSnippet: String?
    val summary: String?
    val pubDate: String?
    val isoDate: String?
    val categories: List<String>?
}