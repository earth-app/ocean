package com.earthapp.util

actual fun getEnv(name: String): String? = System.getenv(name)