package com.vianh.blogtruyen.features.list

import androidx.lifecycle.*
import com.vianh.blogtruyen.data.DataManager
import com.vianh.blogtruyen.data.model.Category
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.data.prefs.AppSettings
import com.vianh.blogtruyen.data.prefs.ListMode
import com.vianh.blogtruyen.features.base.list.items.*
import com.vianh.blogtruyen.features.list.data.CategoryRepo
import com.vianh.blogtruyen.features.list.filter.FilterCategoryItem
import com.vianh.blogtruyen.utils.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.plus
import timber.log.Timber

class HomeViewModel(
    private val dataManager: DataManager,
    categoryRepo: CategoryRepo,
    settings: AppSettings,
) : MangaViewModel(settings, categoryRepo) {

    private val listError = MutableStateFlow<Throwable?>(null)
    private val remoteManga = MutableStateFlow(emptyList<Manga>())
    private val hasNextPage = MutableStateFlow(true)
    val pageReload = MutableLiveData(false)

    // Using share flow to allow emit many times when list not change
    private val filterManga: SharedFlow<List<Manga>> =
        combine(remoteManga, filterCategories, this::filterManga)
            .shareIn(viewModelScope + Dispatchers.Default, SharingStarted.Eagerly, 1)


    override val content = combine(filterManga, listError, hasNextPage, listMode, this::combineContent)
        .asLiveData(viewModelScope.coroutineContext + Dispatchers.Default)


    private var loadPageJob: Job? = null

    var currentPage: Int = 1
        private set

    private val nextPage
        get() = currentPage + 1

    init {
        loadPage(1)

        filterCategories.value = settings.getFilterCategories()
        filterManga
            .withPrevious { prev, new ->
                if (currentPage == 1) {
                    return@withPrevious
                }

                val preSize = prev?.size ?: return@withPrevious
                if (new.size == preSize) {
                    loadPage(nextPage)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun combineContent(mangaList: List<Manga>, error: Throwable?, hasNextPage: Boolean, mode: ListMode): List<ListItem> {
        val type = when (mode) {
            ListMode.GRID -> MangaItem.MANGA_GRID_ITEM
            ListMode.DETAILS_LIST -> MangaItem.MANGA_DETAIL_LIST_ITEM
        }

        val listItem = mangaList.map { MangaItem(it, type) }
        val isFirstPage = mangaList.isEmpty()
        if (error != null) {
            if (isFirstPage) {
                return listOf(ErrorItem(error))
            } else {
                return listItem + ErrorFooterItem(error)
            }
        }

        if (isFirstPage && hasNextPage) {
            return listOf(LoadingItem)
        } else {
            return listItem + LoadingFooterItem
        }
    }

    private fun filterManga(manga: List<Manga>, filters: Set<String>): List<Manga> {
        if (filters.isEmpty())
            return manga

        return manga.filterNot { item -> item.categories.any { filters.contains(it.name) } }
    }


    fun loadPage(offset: Int) {
        if (loadPageJob?.isCompleted == false || !hasNextPage.value) {
            return
        }

        Timber.e("Load page $offset")
        listError.value = null
        loadPageJob = launchJob {

            cancelableCatching {
                val newManga = dataManager
                    .mangaProvider
                    .fetchNewManga(offset)

                currentPage = offset
                hasNextPage.value = newManga.isNotEmpty()
                remoteManga.update {
                    if (offset == 1) {
                        newManga
                    } else {
                        it + newManga
                    }
                }
            }.onFailure {
                listError.value = it
            }

            pageReload.value = false
        }
    }

    override fun loadNextPage() {
        loadPage(nextPage)
    }

    override fun reload() {
        pageReload.value = true
        loadPage(1)
    }
}