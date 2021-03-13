package com.vianh.blogtruyen.ui.mangaDetails

import androidx.lifecycle.MutableLiveData
import com.vianh.blogtruyen.data.DataManager
import com.vianh.blogtruyen.data.model.Chapter
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.ui.base.BaseViewModel

class MangaDetailsViewModel(dataManager: DataManager,var manga: Manga): BaseViewModel(dataManager) {
    val mangaLiveData: MutableLiveData<Manga> = MutableLiveData(manga)
    val chapters: MutableLiveData<List<Chapter>> = MutableLiveData(listOf())

    init {
        loadDetails()
    }

    fun loadDetails() {
        launchLoading {
            val detailsManga = dataManager.mangaProvider.fetchDetailManga(manga)
            manga = detailsManga
            mangaLiveData.value = manga
        }
    }

    fun loadChapters() {
        launchJob {
            val mangaChapter = dataManager.mangaProvider.fetchChapterList(manga)
            manga = manga.copy(chapters = mangaChapter)
            chapters.postValue(mangaChapter)
        }
    }
}