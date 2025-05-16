package com.olehmaliuta.clothesadvisor.data.http

import android.content.Context
import com.olehmaliuta.clothesadvisor.types.LocationInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.Locale


object HttpServiceManager {
    const val BASE_URL = "http://10.0.2.2:8000/"
    const val NOMINATIM_SEARCH_URL = "https://nominatim.openstreetmap.org/search"

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

    suspend fun searchLocations(
        query: String,
        limit: Int
    ): List<LocationInfo>? = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(
                    NOMINATIM_SEARCH_URL +
                            "?q=$query" +
                            "&format=json" +
                            "&featureType=city" +
                            "&addressdetails=1" +
                            "&namedetails=1" +
                            "&limit=$limit")
                .addHeader("Accept-Language", Locale.getDefault().language)
                .addHeader("User-Agent", "ClothesAdvisor/1.0")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext null

                val jsonArray = JSONArray(response.body?.string() ?: "[]")
                val suggestions = mutableListOf<LocationInfo>()

                for (i in 0 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(i)
                    val localLocationName = item.getString("name")
                    val engLocationName = try {
                        item.getJSONObject("namedetails").getString("name:en")
                    } catch (_: Exception) {
                        continue
                    }

                    suggestions.add(
                        LocationInfo(
                            nameEng = engLocationName,
                            nameLocal = localLocationName,
                            country = item.getJSONObject("address").getString("country")
                        )
                    )
                }

                suggestions
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