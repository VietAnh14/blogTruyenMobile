package com.vianh.blogtruyen.features.list

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.vianh.blogtruyen.data.DataManager
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.data.prefs.AppSettings
import com.vianh.blogtruyen.data.prefs.ListMode
import com.vianh.blogtruyen.features.base.BaseVM
import com.vianh.blogtruyen.features.base.list.items.EmptyItem
import com.vianh.blogtruyen.features.base.list.items.ListItem
import com.vianh.blogtruyen.features.base.list.items.LoadingFooterItem
import com.vianh.blogtruyen.features.list.data.CategoryRepo
import com.vianh.blogtruyen.features.list.filter.FilterCategoryItem
import com.vianh.blogtruyen.utils.SingleLiveEvent
import com.vianh.blogtruyen.utils.asLiveDataDistinct
import com.vianh.blogtruyen.utils.mapToSet
import com.vianh.blogtruyen.utils.withPrevious
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.plus
import timber.log.Timber

class HomeViewModel(
    private val dataManager: DataManager,
    private val categoryRepo: CategoryRepo,
    private val appSettings: AppSettings
) : BaseVM() {

    private val remoteManga: MutableStateFlow<List<Manga>> = MutableStateFlow(emptyList())
    private val listMode = MutableStateFlow(appSettings.getListMode())
    private val filterCategories = MutableStateFlow(appSettings.getFilterCategories())
    private val filterManga = combine(remoteManga, filterCategories)
    { manga, filters ->
        filterManga(manga, filters)
    }.shareIn(viewModelScope + Dispatchers.Default, SharingStarted.Eagerly, 1)

    val categories = categoryRepo.observeAll().distinctUntilChanged()
    val categoryItems = combine(filterCategories, categories)
    { filterCategories, categories ->
        val newList = categories
            .mapTo(ArrayList()) { FilterCategoryItem(it, filterCategories.contains(it.name)) }
        newList.sortBy { it.category.name.lowercase() }
        return@combine newList
    }.asLiveDataDistinct(Dispatchers.Default)

    val content = combine(filterManga, listMode)
    { mangaList, mode ->
        combineContent(mangaList, mode)
    }.asLiveData(viewModelScope.coroutineContext)

    private var loadPageJob: Job? = null
    val pageReload = SingleLiveEvent(false)
    var currentPage: Int = 1
    val nextPage
        get() = currentPage + 1

    init {
        loadPage(1)

        filterManga
            .withPrevious { prev, new ->
                if (currentPage == 1) {
                    return@withPrevious
                }

                val preSize = prev?.size ?: 0
                if (new.size == preSize) {
                    loadPage(nextPage)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun combineContent(
        mangaList: List<Manga>,
        listMode: ListMode
    ): List<ListItem> {
        val type = when (listMode) {
            ListMode.GRID -> MangaItem.MANGA_GRID_ITEM
            ListMode.DETAILS_LIST -> MangaItem.MANGA_DETAIL_LIST_ITEM
        }

        val filterItems = mangaList.map { MangaItem(it, type) }
        return if (filterItems.isEmpty()) listOf(EmptyItem()) else filterItems + LoadingFooterItem
    }

    private fun filterManga(manga: List<Manga>, filters: Set<String>): List<Manga> {
        if (filters.isEmpty())
            return manga

        return manga.filterNot { item -> item.categories.any { filters.contains(it.name) } }
    }


    fun loadPage(offset: Int = currentPage + 1) {
        if (loadPageJob?.isCompleted == false) {
            return
        }

        Timber.e("Load page $offset")

        loadPageJob = launchLoading {
            if (offset == 1) {
                pageReload.setValue(true)
            }

            val newManga = dataManager
                .mangaProvider
                .fetchNewManga(offset)

            remoteManga.update {
                if (offset == 1) {
                    newManga
                } else {
                    it + newManga
                }
            }

            currentPage = offset
            pageReload.setValue(false)
        }
    }

    fun loadNextPage() {
        loadPage(nextPage)
    }

    fun saveListMode(listMode: ListMode) {
        appSettings.saveListMode(listMode)
        this.listMode.value = listMode
    }


    // ------------------------Filter------------------

    fun applyFilter() {
        if (categoryItems.value!!.all { it.isSelected }) {
            error.postValue(IllegalStateException("You can't filter out all categories"))
            return
        }

        val filterItems = categoryItems.value.orEmpty()
            .filter { it.isSelected }
            .mapToSet { it.category }

        Timber.d("Filter item $filterItems")
        filterCategories.value = filterItems.mapToSet { it.name }
        appSettings.saveFilterCategories(filterItems)
    }
}