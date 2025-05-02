package com.olehmaliuta.clothesadvisor.ui.viewmodels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.olehmaliuta.clothesadvisor.data.http.HttpServiceManager
import com.olehmaliuta.clothesadvisor.data.http.requests.UploadOutfitRequest
import com.olehmaliuta.clothesadvisor.data.http.responses.BaseResponse
import com.olehmaliuta.clothesadvisor.data.http.security.ApiState
import com.olehmaliuta.clothesadvisor.data.http.services.OutfitApiService
import com.olehmaliuta.clothesadvisor.data.database.entities.Outfit
import com.olehmaliuta.clothesadvisor.data.database.entities.query.OutfitWithClothingItemCount
import com.olehmaliuta.clothesadvisor.data.database.entities.query.OutfitWithClothingItems
import com.olehmaliuta.clothesadvisor.data.database.repositories.OutfitDaoRepository
import com.olehmaliuta.clothesadvisor.ui.navigation.StateHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class OutfitViewModel(
    private val repository: OutfitDaoRepository,
    context: Context
) : ViewModel(), StateHandler {
    class Factory(
        private val repository: OutfitDaoRepository,
        private val context: Context
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(OutfitViewModel::class.java)) {
                return OutfitViewModel(
                    repository,
                    context
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    private val service = HttpServiceManager
        .buildService(OutfitApiService::class.java)
    private val sharedPref = context
        .getSharedPreferences("user", Context.MODE_PRIVATE)
    private val gson = Gson()

    var outfitUploadingState by mutableStateOf<ApiState<Long>>(ApiState.Idle)
        private set
    var outfitDeletingState by mutableStateOf<ApiState<Unit>>(ApiState.Idle)
        private set

    var idOfOutfitToEdit = mutableStateOf<Long?>(null)

    val countOutfits = repository.countOutfits()

    override fun restoreState() {
        outfitUploadingState = ApiState.Idle
        outfitDeletingState = ApiState.Idle
    }

    fun getOutfitToEdit(id: Long?): Flow<OutfitWithClothingItems?> {
        return repository.getOutfitWithItemsById(id)
    }

    fun searchOutfits(
        query: String
    ): Flow<List<OutfitWithClothingItemCount>> {
        return repository.searchOutfits(
            query
        )
    }

    fun addOutfit(
        name: String,
        itemIds: List<Long>
    ) {
        viewModelScope.launch {
            outfitUploadingState = ApiState.Loading

            val token = sharedPref.getString("token", null)
            val tokenType = sharedPref.getString("token_type", null)
            var outfit = Outfit(name = name)

            try {
                if (token != null) {
                    val response = service.addClothingCombination(
                        "${tokenType ?: "bearer"} $token",
                        UploadOutfitRequest(name, itemIds)
                    )

                    if (response.isSuccessful) {
                        outfit = Outfit(
                            id = response.body()?.data?.combinationId ?: 0,
                            name = name
                        )
                        sharedPref.edit {
                            putString(
                                "synchronized_at",
                                response.body()?.synchronizedAt)
                        }
                    } else {
                        val errorBody = gson.fromJson(
                            response.errorBody()?.string(),
                            BaseResponse::class.java)
                        outfitUploadingState = ApiState.Error(errorBody.detail)
                        return@launch
                    }
                }

                val newOutfitId = repository.insertOutfitWithItems(outfit, itemIds)
                outfitUploadingState = ApiState.Success(newOutfitId)
            } catch (e: Exception) {
                outfitUploadingState = ApiState.Error("Network error: ${e.message}")
            }
        }
    }

    fun updateOutfit(
        id: Long,
        name: String,
        itemIds: List<Long>
    ) {
        viewModelScope.launch {
            outfitUploadingState = ApiState.Loading

            val token = sharedPref.getString("token", null)
            val tokenType = sharedPref.getString("token_type", null)
            var outfit = Outfit(
                id = id,
                name = name
            )

            try {
                if (token != null) {
                    val response = service.updateClothingCombination(
                        "${tokenType ?: "bearer"} $token",
                        id,
                        UploadOutfitRequest(name, itemIds)
                    )

                    if (response.isSuccessful) {
                        outfit = Outfit(
                            id = response.body()?.data?.combinationId ?: 0,
                            name = name
                        )
                        sharedPref.edit {
                            putString(
                                "synchronized_at",
                                response.body()?.synchronizedAt)
                        }
                    } else {
                        val errorBody = gson.fromJson(
                            response.errorBody()?.string(),
                            BaseResponse::class.java)
                        outfitUploadingState = ApiState.Error(errorBody.detail)
                        return@launch
                    }
                }

                repository.updateOutfitsWithItems(outfit, itemIds)
                outfitUploadingState = ApiState.Success(outfit.id)
            } catch (e: Exception) {
                outfitUploadingState = ApiState.Error("Network error: ${e.message}")
            }
        }
    }

    fun deleteOutfit(
        id: Long
    ) {
        viewModelScope.launch {
            outfitDeletingState = ApiState.Loading

            val token = sharedPref.getString("token", null)
            val tokenType = sharedPref.getString("token_type", null)

            try {
                if (token != null) {
                    val response = service.deleteClothingCombination(
                        "${tokenType ?: "bearer"} $token",
                        id
                    )

                    if (response.isSuccessful) {
                        sharedPref.edit {
                            putString(
                                "synchronized_at",
                                response.body()?.synchronizedAt)
                        }
                    } else {
                        val errorBody = gson.fromJson(
                            response.errorBody()?.string(),
                            BaseResponse::class.java)
                        outfitDeletingState = ApiState.Error(errorBody.detail)
                        return@launch
                    }
                }

                repository.deleteOutfitById(id)
                outfitDeletingState = ApiState.Success(Unit)
            } catch (e: Exception) {
                outfitDeletingState = ApiState.Error("Network error: ${e.message}")
            }
        }
    }
}