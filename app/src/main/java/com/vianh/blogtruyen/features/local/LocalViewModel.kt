package com.vianh.blogtruyen.features.local

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.features.base.BaseVM
import com.vianh.blogtruyen.features.list.MangaItem
import com.vianh.blogtruyen.utils.mapList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow

class LocalViewModel(private val localRepo: LocalSourceRepo): BaseVM() {

    private val content = MutableStateFlow<List<Manga>>(listOf())
    val mangaContent = content
        .mapList { MangaItem(it) }
        .asLiveData(viewModelScope.coroutineContext + Dispatchers.Default)

    init {
        loadMangaList()
    }

    fun loadMangaList() {
        launchLoading(Dispatchers.IO) {
            content.value = localRepo.getMangaList()
        }
    }
}