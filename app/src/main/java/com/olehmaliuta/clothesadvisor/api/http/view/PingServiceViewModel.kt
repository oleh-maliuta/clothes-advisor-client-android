package com.olehmaliuta.clothesadvisor.api.http.view

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.olehmaliuta.clothesadvisor.api.http.HttpServiceManager
import com.olehmaliuta.clothesadvisor.api.http.services.PingService
import kotlinx.coroutines.launch

class PingServiceViewModel : ViewModel() {
    private val service = HttpServiceManager.buildService(PingService::class.java)

    fun ping() {
        viewModelScope.launch {
            try {
                val response = service.ping()
                if (response.isSuccessful) {
                    Log.i("HTTP Ping Message", response.body()?.ping.toString())
                } else {
                    Log.e("Api failed", "HTTP Response Code = ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("Api failed", e.toString())
            }
        }
    }
}