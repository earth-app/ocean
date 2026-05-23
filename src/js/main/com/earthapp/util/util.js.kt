package com.earthapp.util

private external val process: dynamic

actual fun getEnv(name: String): String? {
    val proc = process ?: return null
    val env = proc.env ?: return null
    return (env[name] as? String)?.takeIf { it.isNotEmpty() }
}
