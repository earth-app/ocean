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