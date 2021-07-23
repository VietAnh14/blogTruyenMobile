package com.vianh.blogtruyen.features.home

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.vianh.blogtruyen.data.DataManager
import com.vianh.blogtruyen.features.base.BaseVM
import com.vianh.blogtruyen.features.home.list.MangaItem
import com.vianh.blogtruyen.utils.SingleLiveEvent
import kotlinx.coroutines.flow.MutableStateFlow

class HomeViewModel(private val dataManager: DataManager) : BaseVM() {

    private val listContent: MutableStateFlow<List<MangaItem>> = MutableStateFlow(mutableListOf())
    val content = listContent.asLiveData(viewModelScope.coroutineContext)

    val pageReload = SingleLiveEvent(false)
    var page: Int = 1

    init {
        loadPage()
    }


    fun loadPage(offset: Int = page, reload: Boolean = false) {
        if (isLoading.value == true) {
            return
        }

        launchLoading {
            if (offset == 1) {
                pageReload.setValue(true)
            }

            val mangaItems = dataManager
                .mangaProvider
                .fetchNewManga(offset)
                .map { MangaItem(it) }

            page = offset + 1
            val newList = if (reload) mangaItems else listContent.value.plus(mangaItems)
            listContent.value = newList

            pageReload.setValue(false)
        }
    }
}