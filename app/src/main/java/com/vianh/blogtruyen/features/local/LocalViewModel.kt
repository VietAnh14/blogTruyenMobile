package com.vianh.blogtruyen.features.local

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.features.base.BaseVM
import com.vianh.blogtruyen.features.base.list.items.EmptyItem
import com.vianh.blogtruyen.features.list.MangaItem
import com.vianh.blogtruyen.utils.SingleLiveEvent
import com.vianh.blogtruyen.utils.ext.ifEmpty
import com.vianh.blogtruyen.utils.ext.mapList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow

class LocalViewModel(private val localRepo: LocalSourceRepo): BaseVM() {

    val deleteCompleteEvent = SingleLiveEvent<String>()
    private val content = MutableStateFlow<List<Manga>>(listOf())
    val mangaContent = content
        .mapList { MangaItem(it, MangaItem.MANGA_GRID_ITEM) }
        .ifEmpty { listOf(EmptyItem(message = "Empty downloaded manga")) }
        .asLiveData(viewModelScope.coroutineContext + Dispatchers.Default)

    init {
        loadMangaList()
    }

    fun loadMangaList() {
        launchLoading(Dispatchers.IO) {
            content.value = localRepo.getMangaList()
        }
    }

    fun deleteLocalManga(manga: Manga) {
        launchJob(Dispatchers.IO) {
            localRepo.deleteLocalManga(manga)
            deleteCompleteEvent.postValue(manga.title)
        }
    }
}