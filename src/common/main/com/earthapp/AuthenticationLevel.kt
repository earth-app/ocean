package com.earthapp

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * Represents the level of authentication used within the Earth App.
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
enum class AuthenticationLevel {

    /**
     * The user is not authenticated.
     */
    ANONYMOUS,

    /**
     * The user is authenticated with a basic level of access.
     */
    USER,

    /**
     * The user is authenticated with a higher level of access for its own data.
     */
    OWN_DATA,

    /**
     * The user is an administrator with elevated privileges.
     */
    ADMIN

}