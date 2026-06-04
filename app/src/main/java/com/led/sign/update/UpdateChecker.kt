package com.led.sign.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.URL

data class UpdateInfo(
    val version: String,
    val downloadUrl: String,
    val body: String
)

object UpdateChecker {

    private const val REPO_OWNER = "Tosencen"
    private const val REPO_NAME = "XMLED"
    private const val CURRENT_VERSION = "1.0.1"

    suspend fun checkUpdate(): UpdateInfo? = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://api.github.com/repos/$REPO_OWNER/$REPO_NAME/releases/latest")
            val connection = url.openConnection()
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            val response = connection.inputStream.bufferedReader().use { it.readText() }

            val json = JSONObject(response)
            val tagName = json.getString("tag_name").trimStart('v')
            val body = json.optString("body", "")
            val assets = json.getJSONArray("assets")

            if (tagName > CURRENT_VERSION && assets.length() > 0) {
                val apkUrl = assets.getJSONObject(0).getString("browser_download_url")
                UpdateInfo(version = tagName, downloadUrl = apkUrl, body = body)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun downloadAndInstall(context: Context, downloadUrl: String) = withContext(Dispatchers.IO) {
        try {
            val url = URL(downloadUrl)
            val connection = url.openConnection()
            connection.connectTimeout = 10000
            connection.readTimeout = 30000

            val file = File(context.cacheDir, "update.apk")
            url.openStream().use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            withContext(Dispatchers.Main) {
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/vnd.android.package-archive")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
