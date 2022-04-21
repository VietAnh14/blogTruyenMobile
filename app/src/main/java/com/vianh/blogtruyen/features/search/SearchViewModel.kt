package com.vianh.blogtruyen.features.search

import com.vianh.blogtruyen.data.remote.MangaProvider
import com.vianh.blogtruyen.features.base.BaseVM
import com.vianh.blogtruyen.features.base.list.items.*
import com.vianh.blogtruyen.features.feed.list.NewFeedItem
import com.vianh.blogtruyen.features.list.MangaItem
import com.vianh.blogtruyen.utils.ext.asLiveDataDistinct
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import timber.log.Timber

class SearchViewModel(private val provider: MangaProvider) : BaseVM() {
    private val listContent = MutableStateFlow<List<ListItem>>(emptyList())
    private val pageError = MutableStateFlow<Throwable?>(null)
    private val hasNextPage = MutableStateFlow(false)

    val contents = combine(listContent, pageError, hasNextPage) { newContents, error, hasNext ->
        if (error != null) {
            val isFirstPage = newContents.getOrNull(0)?.viewType == ListItem.LOADING_ITEM
            if (isFirstPage) {
                return@combine listOf(ErrorItem(error))
            } else
                return@combine newContents + ErrorFooterItem(error)
        }

        if (newContents.isEmpty())
            return@combine listOf(EmptyItem())

        if (hasNext)
            return@combine newContents + LoadingFooterItem

        return@combine newContents
    }.asLiveDataDistinct(Dispatchers.Default)

    var pageNumber: Int = 1
    var searchInit = false
    var searchJob: Job? = null

    fun search(query: String?) {
        if (query.isNullOrBlank() || isLoading.value == true)
            return

        hasNextPage.value = false
        searchJob?.cancel()
        searchJob = launchLoading {
            listContent.value = listOf(LoadingItem)
            pageNumber = 1
            search(query, 1)
        }
    }

    fun loadMore(query: String?) {
        if (!hasNextPage.value || isLoading.value == true)
            return

        searchJob = launchLoading {
            search(query, pageNumber + 1)
            pageNumber++
        }
    }

    private suspend fun search(query: String?, index: Int) {
        pageError.value = null
        if (query.isNullOrBlank())
            return

        val searchResult = provider.searchByName(query, index).map {
            MangaItem(it, NewFeedItem.DETAILS_ITEM)
        }

        hasNextPage.value = searchResult.isNotEmpty()
        listContent.value = if (index == 1) searchResult else listContent.value + searchResult
        pageNumber = index
    }

    override fun createExceptionHandler(): CoroutineExceptionHandler {
        return CoroutineExceptionHandler { _, throwable ->
            if (throwable is CancellationException)
                throw throwable

            Timber.e(throwable)
            pageError.value = throwable
        }
    }
}