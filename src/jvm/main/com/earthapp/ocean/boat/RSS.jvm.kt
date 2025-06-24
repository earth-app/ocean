package com.earthapp.ocean.boat

import com.prof18.rssparser.RssParser

actual suspend fun retrieveRSSFeed(url: String): List<Scraper.Page> {
    val parser = RssParser()
    val channel = parser.getRssChannel(url)

    val home = channel.link ?: url.substringAfter("//").substringBefore('/').let { "https://$it" }
    val faviconUrl = url.substringAfter("//").substringBefore("/").let { domain ->
        "https://$domain/favicon.ico"
    }

    val channelTitle = channel.title ?: "RSS Feed"

    return channel.items.mapNotNull { item ->
        val url = item.link ?: item.guid ?: return@mapNotNull null
        val title = item.title ?: return@mapNotNull null

        Scraper.Page(
            url = url,
            title = title,
            author = channelTitle,
            source = channelTitle,
            date = item.pubDate ?: "",
            links = mapOf("Home" to home),
            faviconUrl = faviconUrl
        ).apply {
            abstract = item.description ?: "No summary available."
            content = item.content ?: "No content available."
        }
    }
}