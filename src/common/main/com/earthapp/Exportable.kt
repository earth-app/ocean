@file:OptIn(ExperimentalJsExport::class)

package com.earthapp

import dev.whyoleg.cryptography.BinarySize
import dev.whyoleg.cryptography.BinarySize.Companion.bits
import dev.whyoleg.cryptography.BinarySize.Companion.bytes
import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.AES
import io.github.oshai.kotlinlogging.KotlinLogging
import korlibs.io.compression.compress
import korlibs.io.compression.deflate.GZIP
import korlibs.io.lang.Charsets
import korlibs.io.lang.encodeToByteArray
import kotlinx.serialization.json.Json
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

interface Exportable {

    /**
     * Exports the object to a string format.
     * @return The string representation of the object.
     */
    @JsExport
    fun toJson(): String = json.encodeToString(this)

    /**
     * Exports the object to a binary format.
     * @return The binary representation of the object.
     */
    @JsExport
    fun toBinary(): ByteArray {
        val bytes = toJson().encodeToByteArray(Charsets.UTF8)
        return bytes.compress(GZIP)
    }

    /**
     * Exports the object to a binary format and encrypts it using AES encryption.
     * @return A pair containing the encrypted binary data and the encryption key.
     */
    suspend fun toBinaryEncrypted(): Pair<ByteArray, ByteArray> {
        val data = toBinary()
        val key = keyGenerator.generateKey()
        val cipher = key.cipher(256.bits)

        val encrypted = cipher.encrypt(data)
        val encodedKey = key.encodeToByteArray(AES.Key.Format.RAW)

        return Pair(encrypted, encodedKey)
    }

    companion object {
        private val logger = KotlinLogging.logger("com.earthapp.Exportable")

        private val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
            prettyPrint = true
        }

        private val keyGenerator = CryptographyProvider.Default.get(AES.GCM).keyGenerator(AES.Key.Size.B256)
    }

}