package com.earthapp.util

/**
 * Strips HTML tags from a string.
 * This function uses a regular expression to remove all HTML tags from the input string,
 * leaving only the text content.
 * @return The input string with all HTML tags removed.
 */
fun String.stripHTML(): String {
    return replace(Regex("<[^>]*>"), "")
}