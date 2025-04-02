package com.olehmaliuta.clothesadvisor.api.http

import android.annotation.SuppressLint
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


object HttpServiceManager {
    const val BASE_URL = "https://10.0.2.2:7000/api/"
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(getUnsafeClient())
        .build()

    fun<T> buildService(service: Class<T>): T {
        return retrofit.create(service)
    }

    private fun getUnsafeClient(): OkHttpClient {
        try {
            val trustAllCerts = arrayOf<TrustManager>(@SuppressLint("CustomX509TrustManager")
            object : X509TrustManager {
                @SuppressLint("TrustAllX509TrustManager")
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                @SuppressLint("TrustAllX509TrustManager")
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            }
            )

            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            val sslSocketFactory = sslContext.socketFactory

            val builder: OkHttpClient.Builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            builder.hostnameVerifier { _, _ -> true }

            val okHttpClient: OkHttpClient = builder.build()
            return okHttpClient
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}