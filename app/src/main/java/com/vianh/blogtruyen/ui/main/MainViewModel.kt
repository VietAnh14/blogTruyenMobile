package com.vianh.blogtruyen.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vianh.blogtruyen.Event
import com.vianh.blogtruyen.data.DataManager
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.ui.base.BaseViewModel

class MainViewModel(dataManager: DataManager) : BaseViewModel(dataManager) {
    private val items = MutableLiveData<MutableList<Manga>>(mutableListOf())
    fun items(): LiveData<MutableList<Manga>> = items
    val mangaClickEvent = MutableLiveData<Event<Manga>>()
    var pageNumber = 1

    fun onMangaItemClick(item: Manga) {
        mangaClickEvent.value = Event(item)
    }

    fun getPage(append: Boolean = false) {
        if (isLoading.value == true) {
            return
        }

        launchLoading {
            val newPage = dataManager.getMangaProvider().fetchNewManga(pageNumber)
            if (append) {
                items.value?.addAll(newPage)
                items.value = items.value
                pageNumber = 1
            } else {
                items.value = newPage
            }
            ++pageNumber
        }
    }
}