package io.github.asephermann.plugins.antitampering

import android.content.res.AssetManager
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


internal object AssetsIntegrity {
    private const val MESSAGE_DIGEST_ALGORITHM = "SHA-256"
    private const val ASSETS_BASE_PATH = "www/"
    private val assetsHashes = Collections.unmodifiableMap(
        HashMap<String, String?>()
    )

    @Throws(Exception::class)
    fun check(assets: AssetManager): Int {
        for ((key, value) in assetsHashes.entries) {
            val fileNameDecode: ByteArray = Base64.decode(key, 0)
            val fileName = String(fileNameDecode, StandardCharsets.UTF_8)
            // Log.d("AntiTampering", fileName + " -> " + entry.getValue());
            val filePath = ASSETS_BASE_PATH + fileName
            val file = assets.open(filePath)
            val hash = getFileHash(file)
            if (value == null || value != hash) {
                throw Exception("Content of $fileName has been tampered")
            }
        }
        return assetsHashes.size
    }

    @Throws(IOException::class, NoSuchAlgorithmException::class)
    private fun getFileHash(file: InputStream): String {
        val buffer = ByteArrayOutputStream()
        var nRead: Int
        val data = ByteArray(16384)
        while (file.read(data, 0, data.size).also { nRead = it } != -1) {
            buffer.write(data, 0, nRead)
        }
        buffer.flush()
        val digest = MessageDigest.getInstance(MESSAGE_DIGEST_ALGORITHM)
        val hashBytes = digest.digest(buffer.toByteArray())
        val hexString = StringBuffer()
        for (i in hashBytes.indices) {
            if (0xff and hashBytes[i].toInt() < 0x10) {
                hexString.append("0")
            }
            hexString.append(Integer.toHexString(0xFF and hashBytes[i].toInt()))
        }
        // Log.d("AntiTampering", String(hexString));
        return String(hexString)
    }
}