package com.olehmaliuta.clothesadvisor.tools

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream

object FileTool {
    fun getFileFromUri(context: Context, uri: Uri): File? {
        val contentResolver = context.contentResolver
        val fileExtension = getFileExtension(context, uri)
        val tempFile = File.createTempFile("upload", ".$fileExtension", context.cacheDir)

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

    fun uriToFile(
        resolver: ContentResolver,
        cacheDir: File,
        uri: Uri,
        fileName: String
    ): File? {
        val fileExtension = resolver
            .getType(uri)
            ?.substringAfterLast("/") ?: "jpg"
        val finalFileName = if (fileName.contains('.'))
            fileName else "$fileName.$fileExtension"
        val tempFile = File(cacheDir, finalFileName)

        try {
            resolver.openInputStream(uri)?.use { inputStream ->
                tempFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        } catch (_: FileNotFoundException) {
            return null
        }

        return tempFile
    }

    fun filesToMultipartBodyFiles(
        files: List<File>,
        mediaType: String = "application/octet-stream",
        partName: String = "files"
    ): List<MultipartBody.Part> {
        return files.map { file ->
            val requestBody = file
                .asRequestBody(
                    mediaType.toMediaTypeOrNull()
                )
            MultipartBody.Part.createFormData(
                partName,
                file.name,
                requestBody
            )
        }
    }

    fun persistUriPermission(context: Context, uri: Uri) {
        context.contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    }

    private fun getFileExtension(context: Context, uri: Uri): String {
        return context.contentResolver.getType(uri)?.substringAfterLast('/') ?: "file"
    }
}