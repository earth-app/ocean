package com.earthapp.ocean.boat

/**
 * Represents a collection of RSS feeds.
 */
object RSS {

    /**
     * A map of RSS feed URLs to their associated tags.
     */
    val RSS_FEEDS = mutableMapOf(
        "https://www.chess.com/rss/news" to listOf("chess", "chess.com", "chess news"),
        "https://gmitch215.blog/feed.xml" to listOf("gmitch215", "blog", "personal blog", "tech blog"),
    )

    /**
     * A list of scrapers for the RSS feeds defined in [RSS_FEEDS].
     */
    val SCRAPERS
        get() = RSS_FEEDS.map { (url, tags) ->
            object : Scraper() {
                override val name = "RSS Feed [$url]"
                override val baseUrl: String = url
                override val tags = tags

                override suspend fun search(query: String, pageLimit: Int): List<Page> {
                    val queries = query.split(" ").filter { it.isNotBlank() }
                    return retrieveRSSFeed(url).filter { page ->
                        queries.all { query ->
                            page.title.contains(query, ignoreCase = true) ||
                                    page.abstract.contains(query, ignoreCase = true) ||
                                    page.content.contains(query, ignoreCase = true)
                        }
                    }.take(pageLimit)
                }
            }
        }

}

expect suspend fun retrieveRSSFeed(url: String): List<Scraper.Page>