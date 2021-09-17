package com.vianh.blogtruyen.features.list

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.vianh.blogtruyen.data.DataManager
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.features.base.BaseVM
import com.vianh.blogtruyen.features.base.list.items.EmptyItem
import com.vianh.blogtruyen.features.base.list.items.LoadingFooterItem
import com.vianh.blogtruyen.utils.SingleLiveEvent
import com.vianh.blogtruyen.utils.ifEmpty
import com.vianh.blogtruyen.utils.mapList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class HomeViewModel(private val dataManager: DataManager) : BaseVM() {

    private val remoteManga: MutableStateFlow<List<Manga>> = MutableStateFlow(emptyList())

    val content = remoteManga
        .mapList { MangaItem(it) }
        .map {
            if (it.isEmpty())
                return@map listOf(EmptyItem())

            it + LoadingFooterItem
        }
        .asLiveData(viewModelScope.coroutineContext)

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

            val newManga = dataManager
                .mangaProvider
                .fetchNewManga(offset)

            page = offset + 1

            remoteManga.update {
                if (offset == 1) {
                    newManga
                } else {
                    it + newManga
                }
            }
            pageReload.setValue(false)
        }
    }
}