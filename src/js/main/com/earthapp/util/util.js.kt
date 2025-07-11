package com.earthapp.util

import korlibs.platform.process

actual fun getEnv(name: String): String? = process.env[name]