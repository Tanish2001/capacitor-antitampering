package io.github.asephermann.plugins.antitampering

import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.util.Log
import com.google.gson.Gson
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import kotlin.collections.ArrayList


internal object AssetsIntegrity {
    private const val MESSAGE_DIGEST_ALGORITHM = "SHA-256";
    private const val ASSETS_BASE_PATH = "public/";
    private val assetsHashes = HashMap<String, String?>();
    var hashes = mutableListOf<Map<String, String>>();
    private val result = HashMap<String, String?>();
    var listOfFilesTampered:ArrayList<String> = ArrayList();
    var isTampered:Boolean = false;
    @Throws(Exception::class)
    fun check(activity: Activity, assets: AssetManager, throwExceptionEnabled: Boolean = false): HashMap<String, String?> {

        // Load expected hashes from JSON before loop
        loadHashesFromJSON(assets);
        val root = "public" // Replace with your root directory if needed (e.g., "www/")
        val filesFromAssets = getFilesFromAssets(assets, root);
        val uniqueHashes: Set<Map<String, String>> = hashes.toHashSet()
        val uniqueHashesCopy = uniqueHashes.toList()
        var successCount:Int = 0
        val allFiles = hashes;
        val pm: PackageManager = activity.packageManager
//        var hashes= HashMap<String, String?>();
//        hashes.putAll(getHashesFromAssets(assets, ASSETS_BASE_PATH));
//        assetsHashes.putAll(hashes)

        try {

            var msg = ""
            val pm: PackageManager = activity.packageManager
            val appInfo: PackageInfo =
                    pm.getPackageInfo(activity.packageName, PackageManager.GET_SIGNATURES)
            val sign: String = appInfo.signatures[0].toCharsString()
            if (sign == "") msg += "App not signed\n"

            for (fileInfo in filesFromAssets) {
                val fileName = fileInfo["name"] ?: continue  // Skip if name is null
                val filePath = fileInfo["path"] ?: continue
                if (fileInfo["isDirectory"] == "false") {
                    try {
                        val file = assets.open(filePath)
                        val calculatedHash = getFileHash(file);
                        if (assetsHashes[filePath] != calculatedHash) {
                            msg += "Content of \"$fileName\" \"$filePath\" has been tampered\n";
                            isTampered = true;
                            listOfFilesTampered.add(fileName);
                        } else {
                            successCount++
                            System.out.println("hash match succeed " + fileName)
                        }
                    } catch (e: IOException) {
                        System.out.println(e);
                        Log.e("AssetsIntegrity", "Error reading file: $e")

                    }
                }
                else{
                    System.out.println(filePath+" is a Directory");
                }
            }

//            val alertDialog: AlertDialog = AlertDialog.Builder(activity).create()
//            alertDialog.setTitle("Assets Integrity")
//            alertDialog.setMessage(msg)
//            alertDialog.setButton(
//                    AlertDialog.BUTTON_POSITIVE, "OK"
//            ) { dialog, _ ->
//                dialog.dismiss()
//                if (msg != "") activity.finish()
//                if (throwExceptionEnabled) throw Exception(msg)
//            }
//
//            if (msg != "") alertDialog.show()

        } catch (e: IOException) {
            Log.e("AssetsIntegrity", "Error reading file: $e")
        } catch (e: NoSuchAlgorithmException) {
            Log.e("AssetsIntegrity", "Error creating hash: $e")
        }
        result.put("assetsHashesSize", successCount.toString());
        result.put("isTampered",isTampered.toString());
        var fileName:String = " ";
        listOfFilesTampered.forEach { file:String ->
            run {
                fileName += file + " , ";
            }
        }
        result.put("tamperFileNames",fileName);
        return result;
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
        Log.d("AntiTampering", String(hexString))
        return String(hexString)
    }

    private fun loadHashesFromJSON(assets: AssetManager) {
        try {
            val inputStream: InputStream = assets.open("assets.json")
            val reader: Reader = InputStreamReader(inputStream, StandardCharsets.UTF_8)
            val gson = Gson()
            val map: HashMap<String, String> = gson.fromJson(reader, HashMap::class.java) as HashMap<String, String> // Cast to desired type
            assetsHashes.putAll(map)
        } catch (e: Exception) {
            Log.e("AssetsIntegrity", "Error loading hashes from JSON: $e")
        }
    }


//    fun getFilesFromAssets(mgr: AssetManager, path: String = ""): List<Map<String, String>> {
//        val fileList = mutableListOf<Map<String, String>>()
//        try {
//            val list = mgr.list(path)
//            if (list != null) {
//                val fileListCopy = list.toList();
//                for (fileName in fileListCopy) {
//                    if (fileName == "assets") {
//                        System.out.println(fileName)
//                    }
//                    val fullPath = if (path.isEmpty()) fileName else "$path/$fileName"
//                    val isDirectory = try {
//                        val inputStream = mgr.open(fullPath) // Open for reading
//                        inputStream.close() // Close the input stream
//                        false // Opened and closed successfully, likely a file
//                    } catch (e: IOException) {
////                        Log.e("AssetsIntegrity","sError opening file in getFilesFromAsset method $e");
//                        true // Not a file if opening fails (likely a directory)
//                    }
//                    val fileInfo = mapOf(
//                            "name" to fileName,
//                            "path" to fullPath,
//                            "isDirectory" to isDirectory.toString()
//                    )
//                    fileList.add(fileInfo)
//                    try {
//                        if (isDirectory) {
//                            fileList.addAll(getFilesFromAssets(mgr, fullPath)) // Recursive call
//                        }
//                    }catch (e:IOException){
//                        Log.e("getFilesFromAssets",e.toString());
//                    }
//                }
//            }
//        } catch (e: IOException) {
//            println("Error listing assets: $path")
//        }
//        this.hashes.addAll(fileList);
//         return fileList
//    }

    fun getFilesFromAssets(mgr: AssetManager, path: String = ""): List<Map<String, String>> {
        val fileList = mutableListOf<Map<String, String>>()
        try {
            val list = mgr.list(path)
            if (list != null) {
                val iterator = list.iterator()
                while (iterator.hasNext()) {
                    val fileName = iterator.next()
                    if (fileName == "assets") {
                        System.out.println(fileName)
                    }
                    val fullPath = if (path.isEmpty()) fileName else "$path/$fileName"
                    val isDirectory = try {
                        val inputStream = mgr.open(fullPath)
                        inputStream.close() // Close the input stream
                        false
                    } catch (e: IOException) {
                        true
                    }
                    val fileInfo = mapOf(
                            "name" to fileName,
                            "path" to fullPath,
                            "isDirectory" to isDirectory.toString()
                    )
                    fileList.add(fileInfo)

                    if (isDirectory) {
                        val filesInDirectory = getFilesFromAssets(mgr, fullPath) // Recursive call
                        fileList.addAll(filesInDirectory)
                    }
                }
            }
        } catch (e: IOException) {
            println("Error listing assets: $path")
        }

        return fileList
    }

}