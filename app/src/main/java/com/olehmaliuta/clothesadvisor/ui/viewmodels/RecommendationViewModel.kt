package com.olehmaliuta.clothesadvisor.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.olehmaliuta.clothesadvisor.data.http.HttpServiceManager
import com.olehmaliuta.clothesadvisor.data.http.security.ApiState
import com.olehmaliuta.clothesadvisor.navigation.StateHandler
import com.olehmaliuta.clothesadvisor.types.LocationInfo
import kotlinx.coroutines.launch

class RecommendationViewModel() : ViewModel(), StateHandler {
    class Factory() : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RecommendationViewModel::class.java)) {
                return RecommendationViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    var locationSearchingState by mutableStateOf<ApiState<List<LocationInfo>?>>(ApiState.Idle)
        private set

    override fun restoreState() {
        locationSearchingState = ApiState.Idle
    }

    fun searchLocations(
        query: String,
        limit: Int = 7
    ) {
        viewModelScope.launch {
            locationSearchingState = ApiState.Loading

            locationSearchingState = try {
                val response = HttpServiceManager.searchLocations(query, limit)

                if (response != null) {
                    ApiState.Success(response)
                } else {
                    ApiState.Error("Could not load locations")
                }
            } catch (e: Exception) {
                ApiState.Error("Network error: ${e.message}")
            }
        }
    }
}