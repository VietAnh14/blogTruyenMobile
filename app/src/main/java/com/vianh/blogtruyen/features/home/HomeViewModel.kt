package com.vianh.blogtruyen.features.home

import com.github.michaelbull.result.map
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import com.vianh.blogtruyen.data.DataManager
import com.vianh.blogtruyen.features.base.BaseVM
import com.vianh.blogtruyen.features.home.list.MangaItem
import com.vianh.blogtruyen.utils.SingleLiveEvent
import kotlinx.coroutines.flow.MutableStateFlow

class HomeViewModel(private val dataManager: DataManager) : BaseVM() {

    val listContent: MutableStateFlow<List<MangaItem>> = MutableStateFlow(mutableListOf())
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

            dataManager
                .mangaProvider
                .fetchNewManga(offset)
                .map { items ->
                    items.map { MangaItem(it) }
                }.onSuccess {
                    page = offset + 1
                    val newList = if (reload) it else listContent.value.plus(it)
                    listContent.value = newList
                }.onFailure {
                    error.call(it)
                }

            pageReload.setValue(false)
        }
    }
}