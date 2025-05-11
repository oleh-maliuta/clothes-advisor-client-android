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
import com.olehmaliuta.clothesadvisor.data.http.responses.BaseResponse
import com.olehmaliuta.clothesadvisor.data.http.security.ApiState
import com.olehmaliuta.clothesadvisor.data.http.services.ClothingItemApiService
import com.olehmaliuta.clothesadvisor.data.database.entities.ClothingItem
import com.olehmaliuta.clothesadvisor.data.database.repositories.ClothingItemDaoRepository
import com.olehmaliuta.clothesadvisor.navigation.StateHandler
import com.olehmaliuta.clothesadvisor.utils.FileTool
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class ClothingItemViewModel(
    private val repository: ClothingItemDaoRepository,
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

    var itemUploadingState by mutableStateOf<ApiState<Long>>(ApiState.Idle)
        private set
    var itemDeletingState by mutableStateOf<ApiState<Unit>>(ApiState.Idle)
        private set
    var isFavoriteTogglingState by mutableStateOf<ApiState<Unit>>(ApiState.Idle)
        private set
    var backgroundRemovingState by mutableStateOf<ApiState<File>>(ApiState.Idle)
        private set

    var idOfItemToEdit = mutableStateOf<Long?>(null)

    val countClothingItems = repository.countClothingItems()

    override fun restoreState() {
        (backgroundRemovingState as? ApiState.Success)?.data?.delete()

        itemUploadingState = ApiState.Idle
        itemDeletingState = ApiState.Idle
        isFavoriteTogglingState = ApiState.Idle
        backgroundRemovingState = ApiState.Idle
    }

    fun getItemToEdit(id: Long?): Flow<ClothingItem?> {
        return repository.getItemById(id)
    }

    fun getItemsByIds(ids: List<Long>): Flow<List<ClothingItem>> {
        return repository.getItemsByIds(ids)
    }

    fun getUniqueCategoriesByIds(
        ids: List<Long>
    ): Flow<List<String>> {
        return repository.getUniqueCategoriesByIds(ids)
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

                val newItemId = repository.insertEntity(currentItem)
                itemUploadingState = ApiState.Success(newItemId)
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
                itemUploadingState = ApiState.Success(currentItem.id)
            } catch (e: Exception) {
                itemUploadingState = ApiState.Error("Network error: ${e.message}")
            } finally {
                file?.delete()
            }
        }
    }

    fun updateIsFavoriteValue(
        id: Long
    ) {
        viewModelScope.launch {
            if (isFavoriteTogglingState !is ApiState.Loading) {
                isFavoriteTogglingState = ApiState.Loading
            }

            val token = sharedPref.getString("token", null)
            val tokenType = sharedPref.getString("token_type", null)

            try {
                if (token != null) {
                    val response = service.toggleFavorite(
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
                        if (isFavoriteTogglingState !is ApiState.Error) {
                            val errorBody = gson.fromJson(
                                response.errorBody()?.string(),
                                BaseResponse::class.java)
                            isFavoriteTogglingState = ApiState.Error(errorBody.detail)
                        }
                        return@launch
                    }
                }

                repository.updateIsFavoriteValue(id)
            } catch (e: Exception) {
                if (isFavoriteTogglingState !is ApiState.Error) {
                    isFavoriteTogglingState =
                        ApiState.Error("Network error: ${e.message}")
                }
            }
        }
    }

    fun deleteClothingItem(
        id: Long
    ) {
        viewModelScope.launch {
            itemDeletingState = ApiState.Loading

            val token = sharedPref.getString("token", null)
            val tokenType = sharedPref.getString("token_type", null)

            try {
                if (token != null) {
                    val response = service.deleteClothingItem(
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
                        itemDeletingState = ApiState.Error(errorBody.detail)
                        return@launch
                    }
                }

                repository.deleteItemById(id)
                itemDeletingState = ApiState.Success(Unit)
            } catch (e: Exception) {
                itemDeletingState = ApiState.Error("Network error: ${e.message}")
            }
        }
    }

    fun getImageWithNoBackground(
        context: Context,
        id: Long
    ) {
        viewModelScope.launch {
            backgroundRemovingState = ApiState.Loading

            val token = sharedPref.getString("token", "")
            val tokenType = sharedPref.getString("token_type", null)

            try {
                val file = HttpServiceManager.downloadFileByUrl(
                    context,
                    HttpServiceManager.BASE_URL +
                            "clothing-items/$id/preview-remove-background",
                    "${tokenType ?: "bearer"} $token"
                )

                if (file != null) {
                    backgroundRemovingState = ApiState.Success(file)
                } else {
                    itemDeletingState = ApiState.Error(
                        "Could not remove the background.")
                }
            } catch (e: Exception) {
                itemDeletingState = ApiState.Error("Network error: ${e.message}")
            }
        }
    }

    fun cancelBackgroundRemoving() {
        (backgroundRemovingState as? ApiState.Success)?.data?.delete()
        backgroundRemovingState = ApiState.Idle
    }
}