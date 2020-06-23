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
    val _items = MutableLiveData<List<Manga>>()
    val mangaClickEvent = MutableLiveData<Event<Manga>>()

    fun getListManga() {
        uiScope.launch {
            try {
                val data = dataManager.getMangaProvider().fetchNewManga()
                _items.value = data
                Log.d(TAG, _items.toString())
            } catch (e: Exception) {
                Log.e(TAG, e.message!!)
            }
        }
    }

    fun onMangaItemClick(item: Manga) {
        mangaClickEvent.value = Event(item)
    }
}