package com.olehmaliuta.clothesadvisor.tools

import android.content.ContentResolver
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileNotFoundException

object FileTool {
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
}