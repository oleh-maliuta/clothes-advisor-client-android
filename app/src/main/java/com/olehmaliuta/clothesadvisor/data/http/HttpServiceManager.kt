package com.olehmaliuta.clothesadvisor.data.http

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File


object HttpServiceManager {
    const val BASE_URL = "http://10.0.2.2:8000/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun<T> buildService(service: Class<T>): T {
        return retrofit.create(service)
    }

    suspend fun downloadFileByUrl(
        context: Context,
        url: String,
        authToken: String? = null
    ): File? = withContext(Dispatchers.IO) {
        try {
            val tempClient = OkHttpClient()
            val request = if (authToken != null)
                Request.Builder()
                    .url(url)
                    .header("Authorization", authToken)
                    .build() else
                Request.Builder()
                    .url(url)
                    .build()

            tempClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext null
                val fileName = getFileNameFromResponse(url, response)
                    ?: "download_${System.currentTimeMillis()}"
                val file = File.createTempFile(
                    "temp_${System.currentTimeMillis()}_",
                    getFileExtension(fileName),
                    context.cacheDir
                )

                response.body?.byteStream()?.use { inputStream ->
                    file.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }

                val finalFile = File(file.parent, fileName)
                if (file.renameTo(finalFile)) {
                    finalFile
                } else {
                    file
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getFileNameFromResponse(url: String, response: Response): String? {
        val contentDisposition = response.header("Content-Disposition")
        if (!contentDisposition.isNullOrBlank()) {
            val filenameRegex = "filename=\"?([^\"]+)\"?".toRegex()
            val matchResult = filenameRegex.find(contentDisposition)
            if (matchResult != null) {
                return matchResult.groupValues[1]
            }
        }

        return url.substringAfterLast('/').takeIf { it.isNotBlank() }
    }

    private fun getFileExtension(fileName: String): String {
        return if (fileName.contains('.')) {
            ".${fileName.substringAfterLast('.')}"
        } else {
            ""
        }
    }
}