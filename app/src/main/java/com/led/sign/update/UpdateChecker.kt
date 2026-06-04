package com.led.sign.update

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

data class UpdateInfo(
    val version: String,
    val downloadUrl: String,
    val body: String
)

object UpdateChecker {

    private const val REPO_OWNER = "Tosencen"
    private const val REPO_NAME = "XMLED"
    private const val CURRENT_VERSION = "1.0.2"

    suspend fun checkUpdate(): Result<UpdateInfo?> = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://api.github.com/repos/$REPO_OWNER/$REPO_NAME/releases/latest")
            val connection = url.openConnection() as HttpURLConnection
            connection.setRequestProperty("User-Agent", "XMLED-Android")
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            val responseCode = connection.responseCode
            if (responseCode != 200) {
                return@withContext Result.failure(Exception("HTTP $responseCode"))
            }

            val response = connection.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(response)
            val tagName = json.getString("tag_name").removePrefix("v")
            val body = json.optString("body", "")
            val assets = json.getJSONArray("assets")

            if (tagName > CURRENT_VERSION && assets.length() > 0) {
                val apkUrl = assets.getJSONObject(0).getString("browser_download_url")
                Result.success(UpdateInfo(version = tagName, downloadUrl = apkUrl, body = body))
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun downloadAndInstall(context: Context, downloadUrl: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val url = URL(downloadUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.setRequestProperty("User-Agent", "XMLED-Android")
            connection.connectTimeout = 30000
            connection.readTimeout = 60000

            val file = File(context.cacheDir, "update.apk")
            connection.inputStream.use { input ->
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
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
