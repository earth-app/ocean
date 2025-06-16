@file:JsExport
@file:OptIn(ExperimentalJsExport::class, DelicateCoroutinesApi::class, ExperimentalJsStatic::class)

package com.earthapp.ocean.boat

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise

/**
 * Performs a search using the Scraper class.
 * @param query The search query string.
 * @param pageLimit The maximum number of pages to return. If -1, returns all pages.
 * @return A promise of the search results.
 */
fun Scraper.searchAsPromise(query: String, pageLimit: Int = -1) = GlobalScope.promise { search(query) }

/**
 * Searches for all pages using the Scraper class.
 * @param query The search query string.
 * @param pageLimit The maximum number of pages to return. If -1, returns all pages.
 * @return A promise of the search results.
 */
@JsStatic
fun Scraper.Companion.searchAllAsPromise(query: String, pageLimit: Int = 100) = GlobalScope.promise { searchAll(query, pageLimit) }