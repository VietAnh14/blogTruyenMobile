package com.vianh.blogtruyen.ui.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.vianh.blogtruyen.Event
import com.vianh.blogtruyen.data.DataManager
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.ui.base.BaseViewModel
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.lang.Exception

class MainViewModel(dataManager: DataManager) : BaseViewModel(dataManager) {
    val _items = MutableLiveData<MutableList<Manga>>()
    val mangaClickEvent = MutableLiveData<Event<Manga>>()
    val isLoading = MutableLiveData<Boolean>()
    var pageNumber = 1

    fun getListManga() {
        uiScope.launch {
            isLoading.value = true
            try {
                val data = dataManager.getMangaProvider().fetchNewManga(pageNumber)
                _items.value = data
                pageNumber++
                Log.d(TAG, _items.toString())
            } catch (e: Exception) {
                Log.e(TAG, e.message!!)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun onMangaItemClick(item: Manga) {
        mangaClickEvent.value = Event(item)
    }
}