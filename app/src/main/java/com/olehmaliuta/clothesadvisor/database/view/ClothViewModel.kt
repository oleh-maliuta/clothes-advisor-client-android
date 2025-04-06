package com.olehmaliuta.clothesadvisor.database.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.olehmaliuta.clothesadvisor.App
import com.olehmaliuta.clothesadvisor.database.AppDb

class ClothViewModel(val database: AppDb) : ViewModel() {
    val allClothElements = database.clothDao.getAllClothElements()

    companion object{
        val factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory{
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras): T {
                val database = (checkNotNull(extras[APPLICATION_KEY]) as App).database
                return ClothViewModel(database) as T
            }
        }
    }
}