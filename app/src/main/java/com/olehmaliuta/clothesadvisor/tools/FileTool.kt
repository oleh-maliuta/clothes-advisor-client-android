package com.olehmaliuta.clothesadvisor.tools

import android.content.Context
import android.content.Intent
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

object FileTool {
    fun getFileFromUri(context: Context, uri: Uri): File? {
        val contentResolver = context.contentResolver
        val fileExtension = contentResolver
            .getType(uri)?.substringAfterLast('/') ?: "file"
        val tempFile = File.createTempFile(
            "upload",
            ".$fileExtension",
            context.cacheDir)

        return try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun downloadFileByUrl(context: Context, url: String): File? {
        return withContext(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@withContext null

                    val fileName = url.substringAfterLast('/')
                    val file = File(context.cacheDir, fileName)

                    response.body?.byteStream()?.use { inputStream ->
                        file.outputStream().use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }

                    file
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    fun prepareFilePart(partName: String, file: File): MultipartBody.Part {
        val requestFile = file.asRequestBody(
            "multipart/form-data".toMediaTypeOrNull()
        )

        return MultipartBody.Part.createFormData(
            partName,
            file.name,
            requestFile
        )
    }

    fun filesToMultipartBodyFiles(
        files: List<File>,
        partName: String = "files"
    ): List<MultipartBody.Part> {
        return files.map { file ->
            prepareFilePart(partName, file)
        }
    }

    fun persistUriPermission(context: Context, uri: Uri) {
        context.contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    }
}