package com.earthapp.account

/**
 * Represents a type of account's visibility.
 */
enum class Visibility {
    /**
     * The account is private and not visible to others.
     */
    PRIVATE,
    /**
     * The account is public but not visible in search results.
     */
    UNLISTED,
    /**
     * The account is public and visible to everyone.
     */
    PUBLIC
}