package com.vianh.blogtruyen.features.list

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.vianh.blogtruyen.data.DataManager
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.data.prefs.AppSettings
import com.vianh.blogtruyen.data.prefs.ListMode
import com.vianh.blogtruyen.features.base.BaseVM
import com.vianh.blogtruyen.features.base.list.items.EmptyItem
import com.vianh.blogtruyen.features.base.list.items.LoadingFooterItem
import com.vianh.blogtruyen.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class HomeViewModel(private val dataManager: DataManager, private val appSettings: AppSettings) :
    BaseVM() {

    private val remoteManga: MutableStateFlow<List<Manga>> = MutableStateFlow(emptyList())
    private val listMode = MutableStateFlow(appSettings.getListMode())
    val listModeData = listMode.asLiveData(Dispatchers.Default)

    val content = combine(remoteManga, listMode) { mangaList, mode ->
        combineContent(mangaList, mode)
    }.map {
        if (it.isEmpty())
            return@map listOf(EmptyItem())

        it + LoadingFooterItem
    }.asLiveData(viewModelScope.coroutineContext)

    val pageReload = SingleLiveEvent(false)
    var page: Int = 1

    init {
        loadPage()
    }

    private fun combineContent(mangaList: List<Manga>, listMode: ListMode): List<MangaItem> {
        val type = when(listMode) {
            ListMode.GRID -> MangaItem.MANGA_GRID_ITEM
            ListMode.DETAILS_LIST -> MangaItem.MANGA_DETAIL_LIST_ITEM
        }

        return mangaList.map { MangaItem(it, type) }
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

    fun saveListMode(listMode: ListMode) {
        appSettings.saveListMode(listMode)
        this.listMode.value = listMode
    }
}