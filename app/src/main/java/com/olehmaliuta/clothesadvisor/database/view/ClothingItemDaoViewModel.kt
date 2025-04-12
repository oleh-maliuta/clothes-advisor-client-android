package com.olehmaliuta.clothesadvisor.database.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.olehmaliuta.clothesadvisor.App
import com.olehmaliuta.clothesadvisor.database.AppDb

class ClothingItemDaoViewModel(val database: AppDb) : ViewModel() {
    companion object {
        val factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory{
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras): T {
                val database = (checkNotNull(extras[APPLICATION_KEY]) as App).database
                return ClothingItemDaoViewModel(database) as T
            }
        }
    }

    private val dao = database.clothingItemDao()

    val allClothingItems = dao.getAllClothingItems()
}