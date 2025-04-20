package com.olehmaliuta.clothesadvisor.viewmodels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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

    var itemAddingState by mutableStateOf<ApiState<Unit>>(ApiState.Idle)
        private set

    var idOfItemToEdit = mutableStateOf<Int?>(null)

    val countClothingItems = repository.countClothingItems()

    override fun restoreState() {
        itemAddingState = ApiState.Idle
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
            itemAddingState = ApiState.Loading

            val token = sharedPref.getString("token", null)
            val tokenType = sharedPref.getString("token_type", null)
            var currentItem: ClothingItem = item

            try {
                if (token != null) {
                    val response = service.addClothingItem(
                        "${tokenType ?: "bearer"} $token",
                        FileTool.prepareFilePart("file", file),
                        item.name,
                        item.category,
                        item.season,
                        item.red,
                        item.green,
                        item.blue,
                        item.material,
                        item.brand,
                        item.purchaseDate,
                        item.price,
                        item.isFavorite)

                    if (response.isSuccessful) {
                        currentItem = response.body()?.data
                            ?.toClothingItemDbEntity() ?: item
                    } else {
                        val errorBody = gson.fromJson(
                            response.errorBody()?.string(),
                            BaseResponse::class.java)
                        itemAddingState = ApiState.Error(errorBody.detail)
                        return@launch
                    }
                }

                repository.insertEntity(currentItem)
                itemAddingState = ApiState.Success(Unit)
            } catch (e: Exception) {
                itemAddingState = ApiState.Error("Network error: ${e.message}")
            } finally {
                file.delete()
            }
        }
    }
}