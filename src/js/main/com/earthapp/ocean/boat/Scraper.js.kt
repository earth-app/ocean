@file:JsExport
@file:OptIn(ExperimentalJsExport::class, DelicateCoroutinesApi::class, ExperimentalJsStatic::class)

package com.earthapp.ocean.boat

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import kotlin.js.ExperimentalJsExport
import kotlin.js.ExperimentalJsStatic
import kotlin.js.JsExport
import kotlin.js.JsStatic

/**
 * Searches every registered scraper from JavaScript.
 * @param query The search query string.
 * @param pageLimit The maximum number of pages to return per scraper. Defaults to 100.
 * @return A promise of the aggregated, distinct search results.
 */
@JsStatic
fun Scraper.Companion.searchAllAsPromise(query: String, pageLimit: Int = 100) =
    GlobalScope.promise { searchAll(query, pageLimit) }
