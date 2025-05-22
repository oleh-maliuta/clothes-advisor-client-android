package com.olehmaliuta.clothesadvisor.ui.viewmodels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.olehmaliuta.clothesadvisor.data.http.HttpServiceManager
import com.olehmaliuta.clothesadvisor.data.http.requests.RecommendationRequest
import com.olehmaliuta.clothesadvisor.data.http.responses.BaseResponse
import com.olehmaliuta.clothesadvisor.data.http.responses.RecommendationResponse
import com.olehmaliuta.clothesadvisor.data.http.security.ApiState
import com.olehmaliuta.clothesadvisor.data.http.services.RecommendationApiService
import com.olehmaliuta.clothesadvisor.navigation.StateHandler
import com.olehmaliuta.clothesadvisor.utils.LocaleConstants
import kotlinx.coroutines.launch

class RecommendationViewModel(
    context: Context
) : ViewModel(), StateHandler {
    class Factory(
        private val context: Context
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RecommendationViewModel::class.java)) {
                return RecommendationViewModel(
                    context = context
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    private val service = HttpServiceManager
        .buildService(RecommendationApiService::class.java)
    private val sharedPref = context
        .getSharedPreferences("user", Context.MODE_PRIVATE)
    private val gson = Gson()

    var recommendationState by mutableStateOf<ApiState<RecommendationResponse?>>(ApiState.Idle)
        private set

    override fun restoreState() {
        recommendationState = ApiState.Idle
    }

    fun recommendations(
        request: RecommendationRequest
    ) {
        viewModelScope.launch {
            recommendationState = ApiState.Loading

            val token = sharedPref.getString("token", "")
            val tokenType = sharedPref.getString("token_type", null)

            try {
                val response = service.recommendations(
                    "${tokenType ?: "bearer"} $token",
                    request
                )

                if (response.isSuccessful) {
                    recommendationState = ApiState.Success(response.body()?.data)
                } else {
                    val errorBody = gson.fromJson(
                        response.errorBody()?.string(),
                        BaseResponse::class.java)
                    recommendationState = ApiState.Error(
                        LocaleConstants.getString(errorBody.detail.toString()))
                }
            } catch (e: Exception) {
                recommendationState = ApiState.Error("Network error: ${e.message}")
            }
        }
    }
}