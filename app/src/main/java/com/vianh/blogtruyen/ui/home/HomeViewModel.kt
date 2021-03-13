package com.vianh.blogtruyen.ui.home

import androidx.lifecycle.MutableLiveData
import com.vianh.blogtruyen.data.DataManager
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.ui.base.BaseVM
import com.vianh.blogtruyen.ui.list.ListItem
import com.vianh.blogtruyen.utils.SingleLiveEvent

class HomeViewModel(private val dataManager: DataManager): BaseVM() {

    val listContent: MutableLiveData<List<ListItem>> = MutableLiveData(mutableListOf())
    val pageReload = SingleLiveEvent(false)
    var page: Int = 1

    init {
        loadPage()
    }


    fun loadPage(offset: Int = page, append: Boolean = false) {
        if (isLoading.value == true) {
            return
        }

        launchLoading {
            try {
                if (offset == 1) {
                    pageReload.postValue(true)
                }
                val items = dataManager.mangaProvider.fetchNewManga(offset).map { MangaItem(it) }
                if (append) {
                    val newList = listContent.value?.plus(items)
                    listContent.value = newList
                } else {
                    listContent.value = ArrayList(items)
                }
                page = offset + 1
            } finally {
                pageReload.postValue(false)
            }
        }
    }


    fun com.vianh.blogtruyen.data.local.entity.Manga.toModel(): Manga {
        return Manga(
            id = mangaId,
            imageUrl = imageUrl,
            title = title,
            uploadTitle = uploadTitle,
            description = description,
            link = link,
            subscribed = false,
            categories = setOf(),
            chapters = listOf()
        )
    }
}