package com.vianh.blogtruyen.features.list

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.vianh.blogtruyen.data.DataManager
import com.vianh.blogtruyen.features.base.BaseVM
import com.vianh.blogtruyen.utils.SingleLiveEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow

class HomeViewModel(private val dataManager: DataManager) : BaseVM() {

    private val listContent: MutableStateFlow<List<MangaItem>> = MutableStateFlow(mutableListOf())
    val content = listContent.asLiveData(viewModelScope.coroutineContext)

    val pageReload = SingleLiveEvent(false)
    var page: Int = 1

    init {
        loadPage()
    }


    fun loadPage(offset: Int = page) {
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

            val newList = if (offset == 1) mangaItems else listContent.value.plus(mangaItems)

            listContent.value = newList
            page = offset + 1
            pageReload.setValue(false)
        }
    }
}