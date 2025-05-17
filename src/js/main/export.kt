import com.earthapp.Exportable
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import kotlin.js.Promise

/**
 * Exports the object to a binary format and encrypts it using AES encryption.
 * @return A pair containing the encrypted binary data and the encryption key. The data is the first element
 * and the key is the second element.
 */
@OptIn(DelicateCoroutinesApi::class, ExperimentalJsExport::class)
@JsExport
fun Exportable.toBinaryEncryptedAsPromise(): Promise<List<ByteArray>> = GlobalScope.promise { toBinaryEncrypted().toList() }

/**
 * Creates an Exportable object from binary data and a key.
 * @param data The binary data to decrypt.
 * @param key The key used for decryption.
 * @return The decrypted Exportable object.
 */
@OptIn(DelicateCoroutinesApi::class, ExperimentalJsExport::class)
@JsExport
fun Exportable.Companion.fromBinaryEncryptedAsPromise(
    data: ByteArray,
    key: ByteArray,
): Promise<Exportable> = GlobalScope.promise { fromBinaryEncrypted(data, key) }