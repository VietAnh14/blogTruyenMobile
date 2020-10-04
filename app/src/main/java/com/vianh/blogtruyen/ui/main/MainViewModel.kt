package com.vianh.blogtruyen.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vianh.blogtruyen.Event
import com.vianh.blogtruyen.data.DataManager
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class MainViewModel(dataManager: DataManager) : BaseViewModel(dataManager) {
    private val _items = MutableLiveData<MutableList<Manga>>()
    val item: LiveData<MutableList<Manga>> = _items
    val mangaClickEvent = MutableLiveData<Event<Manga>>()
    val isLoading = MutableLiveData<Boolean>()
    var pageNumber = 1

    val refresh = MutableLiveData<MutableList<Manga>>()

    fun getListManga() {
        uiScope.launch {
            isLoading.value = true
            try {
                val data = dataManager.getMangaProvider().fetchNewManga(pageNumber)
                _items.value = data
                pageNumber++
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading.value = false
            }
        }
    }

    fun onMangaItemClick(item: Manga) {
        mangaClickEvent.value = Event(item)
    }

    fun fetchNew() {
        pageNumber = 1
        uiScope.launch {
            try {
                refresh.value = dataManager.getMangaProvider().fetchNewManga(1)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {

            }
        }
    }
}