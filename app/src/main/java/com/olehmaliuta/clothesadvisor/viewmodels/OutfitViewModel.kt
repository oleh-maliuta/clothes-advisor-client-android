package com.olehmaliuta.clothesadvisor.viewmodels

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.olehmaliuta.clothesadvisor.api.http.HttpServiceManager
import com.olehmaliuta.clothesadvisor.api.http.services.OutfitApiService
import com.olehmaliuta.clothesadvisor.database.entities.ClothingItem
import com.olehmaliuta.clothesadvisor.database.entities.query.OutfitWithClothingItemCount
import com.olehmaliuta.clothesadvisor.database.entities.query.OutfitWithClothingItems
import com.olehmaliuta.clothesadvisor.database.repositories.OutfitDaoRepository
import com.olehmaliuta.clothesadvisor.navigation.StateHandler
import kotlinx.coroutines.flow.Flow

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

    var idOfOutfitToEdit = mutableStateOf<Int?>(null)

    val countOutfits = repository.countOutfits()

    override fun restoreState() {}

    fun getOutfitToEdit(id: Int?): Flow<OutfitWithClothingItems?> {
        return repository.getOutfitWithItemsById(id)
    }

    fun searchOutfits(
        query: String
    ): Flow<List<OutfitWithClothingItemCount>> {
        return repository.searchOutfits(
            query
        )
    }
}