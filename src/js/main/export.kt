@file:JsExport
@file:OptIn(ExperimentalJsExport::class)

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

// Top-level JS entry point.
//
// All public surface is exposed through the `com.earthapp.*` namespace.
// Consumers should use:
//   - `com.earthapp.Exportable.Companion.fromJson(...)` for deserialization
//   - `com.earthapp.ocean.boat.RSS` to build RSS sources
//   - `com.earthapp.ocean.boat.Scraper.Companion.searchAllAsPromise(...)` for article search
//   - `com.earthapp.ocean.recommendActivity(...)` for activity recommendations
