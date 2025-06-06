@file:JsExport
@file:OptIn(ExperimentalJsExport::class, DelicateCoroutinesApi::class)

package com.earthapp.ocean.boat

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise

/**
 * Performs a search using the Scraper class.
 * @param query The search query string.
 * @return A promise of the search results.
 */
fun Scraper.searchAsPromise(query: String) = GlobalScope.promise { search(query) }