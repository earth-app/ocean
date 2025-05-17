@file:OptIn(ExperimentalJsExport::class)

package com.earthapp

import dev.whyoleg.cryptography.BinarySize.Companion.bits
import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.AES
import io.github.oshai.kotlinlogging.KotlinLogging
import korlibs.io.compression.compress
import korlibs.io.compression.deflate.GZIP
import korlibs.io.compression.uncompress
import korlibs.io.lang.Charsets
import korlibs.io.lang.decodeToString
import korlibs.io.lang.encodeToByteArray
import kotlinx.serialization.json.Json
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@JsExport
interface Exportable {

    /**
     * The unique identifier for the object.
     */
    val id: String

    /**
     * Validates its own state before storage. This method is not intended
     * to be overridden. Override the [validate0] method instead.
     */
    fun validate() {
        require(id.isNotEmpty()) { "ID must not be empty." }
        validate0()
    }

    /**
     * Validates its own state before storage.
     * Should throw an [IllegalStateException] if the state is invalid.
     */
    fun validate0()

    /**
     * Exports the object to a string format.
     * @return The string representation of the object.
     */
    fun toJson(): String = json.encodeToString(this)

    /**
     * Exports the object to a binary format.
     * @return The binary representation of the object.
     */
    fun toBinary(): ByteArray {
        val bytes = toJson().encodeToByteArray(Charsets.UTF8)
        return bytes.compress(GZIP)
    }

    /**
     * Exports the object to a binary format and encrypts it using AES encryption.
     * @return A pair containing the encrypted binary data and the encryption key.
     */
    @JsExport.Ignore
    suspend fun toBinaryEncrypted(): Pair<ByteArray, ByteArray> {
        val data = toBinary()
        val key = keyGenerator.generateKey()
        val cipher = key.cipher(CIPHER_SIZE.bits)

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

        /**
         * The size of the cipher used for encryption and decryption.
         * Should be 256 bits for AES-256 encryption.
         */
        const val CIPHER_SIZE = 256

        private val aesGcm = CryptographyProvider.Default.get(AES.GCM)
        private val keyGenerator = aesGcm.keyGenerator(AES.Key.Size.B256)
        private val keyDecoder = aesGcm.keyDecoder()

        /**
         * Deserializes a JSON string into an object of the specified type.
         */
        fun fromJson(jsonString: String): Exportable = json.decodeFromString(jsonString)

        /**
         * Deserializes a binary string into an object of the specified type.
         */
        fun fromBinary(binary: ByteArray): Exportable {
            val decompressed = binary.uncompress(GZIP)
            val jsonString = decompressed.decodeToString(Charsets.UTF8)
            return fromJson(jsonString)
        }

        /**
         * Creates an Exportable object from binary data and a key.
         * @param encrypted The binary data to decrypt.
         * @param encodedKey The key used for decryption.
         * @return The decrypted Exportable object.
         */
        @JsExport.Ignore
        suspend fun fromBinaryEncrypted(encrypted: ByteArray, encodedKey: ByteArray): Exportable {
            val decodedKey = keyDecoder.decodeFromByteArray(AES.Key.Format.RAW, encodedKey)
            val cipher = decodedKey.cipher(CIPHER_SIZE.bits)
            val decrypted = cipher.decrypt(encrypted)
            return fromBinary(decrypted)
        }
    }

}