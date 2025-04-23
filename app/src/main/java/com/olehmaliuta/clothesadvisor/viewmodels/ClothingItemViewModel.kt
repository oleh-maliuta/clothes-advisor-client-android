package com.olehmaliuta.clothesadvisor.viewmodels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.olehmaliuta.clothesadvisor.api.http.HttpServiceManager
import com.olehmaliuta.clothesadvisor.api.http.responses.BaseResponse
import com.olehmaliuta.clothesadvisor.api.http.security.ApiState
import com.olehmaliuta.clothesadvisor.api.http.services.ClothingItemApiService
import com.olehmaliuta.clothesadvisor.database.entities.ClothingItem
import com.olehmaliuta.clothesadvisor.database.repositories.ClothingItemDaoRepository
import com.olehmaliuta.clothesadvisor.navigation.StateHandler
import com.olehmaliuta.clothesadvisor.tools.FileTool
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class ClothingItemViewModel(
    val repository: ClothingItemDaoRepository,
    context: Context
) : ViewModel(), StateHandler {
    class Factory(
        private val repository: ClothingItemDaoRepository,
        private val context: Context
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ClothingItemViewModel::class.java)) {
                return ClothingItemViewModel(
                    repository,
                    context
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    private val service = HttpServiceManager
        .buildService(ClothingItemApiService::class.java)
    private val sharedPref = context
        .getSharedPreferences("user", Context.MODE_PRIVATE)
    private val gson = Gson()

    var itemUploadingState by mutableStateOf<ApiState<Unit>>(ApiState.Idle)
        private set
    var itemDeletingState by mutableStateOf<ApiState<Unit>>(ApiState.Idle)
        private set
    var isFavoriteTogglingState by mutableStateOf<ApiState<Unit>>(ApiState.Idle)
        private set

    var idOfItemToEdit = mutableStateOf<Int?>(null)

    val countClothingItems = repository.countClothingItems()

    override fun restoreState() {
        itemUploadingState = ApiState.Idle
        itemDeletingState = ApiState.Idle
        isFavoriteTogglingState = ApiState.Idle
    }

    fun getItemToEdit(id: Int?): Flow<ClothingItem?> {
        return repository.getItemById(id)
    }

    fun searchItems(
        query: String,
        sortBy: String,
        ascSort: Boolean,
        categories: List<String>,
        seasons: List<String>
    ): Flow<List<ClothingItem>> {
        return repository.searchItems(
            query,
            sortBy,
            ascSort,
            categories,
            seasons
        )
    }

    fun addClothingItem(
        file: File,
        item: ClothingItem
    ) {
        viewModelScope.launch {
            itemUploadingState = ApiState.Loading

            val token = sharedPref.getString("token", null)
            val tokenType = sharedPref.getString("token_type", null)
            var currentItem: ClothingItem = item

            try {
                if (token != null) {
                    val response = service.addClothingItem(
                        "${tokenType ?: "bearer"} $token",
                        FileTool.prepareFilePart("file", file),
                        item.name
                            .toRequestBody("text/plain".toMediaTypeOrNull()),
                        item.category
                            .toRequestBody("text/plain".toMediaTypeOrNull()),
                        item.season
                            .toRequestBody("text/plain".toMediaTypeOrNull()),
                        item.red.toString()
                            .toRequestBody("text/plain".toMediaTypeOrNull()),
                        item.green.toString()
                            .toRequestBody("text/plain".toMediaTypeOrNull()),
                        item.blue.toString()
                            .toRequestBody("text/plain".toMediaTypeOrNull()),
                        item.material
                            .toRequestBody("text/plain".toMediaTypeOrNull()),
                        item.brand
                            ?.toRequestBody("text/plain".toMediaTypeOrNull()),
                        item.purchaseDate
                            ?.toRequestBody("text/plain".toMediaTypeOrNull()),
                        item.price?.toString()
                            ?.toRequestBody("text/plain".toMediaTypeOrNull()),
                        item.isFavorite.toString()
                            .toRequestBody("text/plain".toMediaTypeOrNull()))

                    if (response.isSuccessful) {
                        currentItem = response.body()?.data
                            ?.toClothingItemDbEntity() ?: item
                        sharedPref.edit {
                            putString(
                                "synchronized_at",
                                response.body()?.synchronizedAt)
                        }
                    } else {
                        val errorBody = gson.fromJson(
                            response.errorBody()?.string(),
                            BaseResponse::class.java)
                        itemUploadingState = ApiState.Error(errorBody.detail)
                        return@launch
                    }
                }

                repository.insertEntity(currentItem)
                itemUploadingState = ApiState.Success(Unit)
            } catch (e: Exception) {
                itemUploadingState = ApiState.Error("Network error: ${e.message}")
            } finally {
                file.delete()
            }
        }
    }

    fun updateClothingItem(
        file: File?,
        item: ClothingItem
    ) {
        viewModelScope.launch {
            itemUploadingState = ApiState.Loading

            val token = sharedPref.getString("token", null)
            val tokenType = sharedPref.getString("token_type", null)
            var currentItem: ClothingItem = item

            try {
                if (token != null) {
                    val response = service.updateClothingItem(
                        "${tokenType ?: "bearer"} $token",
                        item.id,
                        if (file != null)
                            FileTool.prepareFilePart("file", file) else null,
                        item.name
                            .toRequestBody("text/plain".toMediaTypeOrNull()),
                        item.category
                            .toRequestBody("text/plain".toMediaTypeOrNull()),
                        item.season
                            .toRequestBody("text/plain".toMediaTypeOrNull()),
                        item.red.toString()
                            .toRequestBody("text/plain".toMediaTypeOrNull()),
                        item.green.toString()
                            .toRequestBody("text/plain".toMediaTypeOrNull()),
                        item.blue.toString()
                            .toRequestBody("text/plain".toMediaTypeOrNull()),
                        item.material
                            .toRequestBody("text/plain".toMediaTypeOrNull()),
                        item.brand
                            ?.toRequestBody("text/plain".toMediaTypeOrNull()),
                        item.purchaseDate
                            ?.toRequestBody("text/plain".toMediaTypeOrNull()),
                        item.price?.toString()
                            ?.toRequestBody("text/plain".toMediaTypeOrNull()),
                        item.isFavorite.toString()
                            .toRequestBody("text/plain".toMediaTypeOrNull()))

                    if (response.isSuccessful) {
                        currentItem = response.body()?.data
                            ?.toClothingItemDbEntity() ?: item
                        sharedPref.edit {
                            putString(
                                "synchronized_at",
                                response.body()?.synchronizedAt)
                        }
                    } else {
                        val errorBody = gson.fromJson(
                            response.errorBody()?.string(),
                            BaseResponse::class.java)
                        itemUploadingState = ApiState.Error(errorBody.detail)
                        return@launch
                    }
                }

                repository.updateEntity(currentItem)
                itemUploadingState = ApiState.Success(Unit)
            } catch (e: Exception) {
                itemUploadingState = ApiState.Error("Network error: ${e.message}")
            } finally {
                file?.delete()
            }
        }
    }

    fun updateIsFavoriteValue(
        id: Int
    ) {
        viewModelScope.launch {
            if (isFavoriteTogglingState !is ApiState.Loading) {
                isFavoriteTogglingState = ApiState.Loading
            }

            val token = sharedPref.getString("token", "")
            val tokenType = sharedPref.getString("token_type", null)

            try {
                val response = service.toggleFavorite(
                    "${tokenType ?: "bearer"} $token",
                    id
                )

                if (response.isSuccessful) {
                    repository.updateIsFavoriteValue(id)
                    sharedPref.edit {
                        putString(
                            "synchronized_at",
                            response.body()?.synchronizedAt)
                    }
                } else {
                    if (isFavoriteTogglingState !is ApiState.Error) {
                        val errorBody = gson.fromJson(
                            response.errorBody()?.string(),
                            BaseResponse::class.java)
                        isFavoriteTogglingState = ApiState.Error(errorBody.detail)
                    }
                }
            } catch (e: Exception) {
                if (isFavoriteTogglingState !is ApiState.Error) {
                    isFavoriteTogglingState =
                        ApiState.Error("Network error: ${e.message}")
                }
            }
        }
    }

    fun deleteClothingItem(
        id: Int
    ) {
        viewModelScope.launch {
            itemDeletingState = ApiState.Loading

            val token = sharedPref.getString("token", "")
            val tokenType = sharedPref.getString("token_type", null)

            try {
                val response = service.deleteClothingItem(
                    "${tokenType ?: "bearer"} $token",
                    id
                )

                if (response.isSuccessful) {
                    repository.deleteItemById(id)
                    sharedPref.edit {
                        putString(
                            "synchronized_at",
                            response.body()?.synchronizedAt)
                    }
                    itemDeletingState = ApiState.Success(Unit)
                } else {
                    val errorBody = gson.fromJson(
                        response.errorBody()?.string(),
                        BaseResponse::class.java)
                    itemDeletingState = ApiState.Error(errorBody.detail)
                }
            } catch (e: Exception) {
                itemDeletingState = ApiState.Error("Network error: ${e.message}")
            }
        }
    }
}