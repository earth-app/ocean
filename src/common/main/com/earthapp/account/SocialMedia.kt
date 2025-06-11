package com.earthapp.account

import kotlinx.serialization.Serializable
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * Represents different social media platform connections for the Earth App.
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
@Serializable
enum class SocialMedia(
    val urlPrefix: String,
) {

    INSTAGRAM("https://www.instagram.com/"),
    FACEBOOK("https://www.facebook.com/"),
    TWITTER("https://twitter.com/"),

}