package com.vianh.blogtruyen.features.favorites

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.vianh.blogtruyen.data.model.Favorite
import com.vianh.blogtruyen.features.base.BaseVM
import com.vianh.blogtruyen.features.base.list.items.EmptyItem
import com.vianh.blogtruyen.features.base.list.items.LoadingItem
import com.vianh.blogtruyen.features.favorites.data.FavoriteRepository
import com.vianh.blogtruyen.features.list.MangaItem
import com.vianh.blogtruyen.utils.ifEmpty
import com.vianh.blogtruyen.utils.mapList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class FavoriteViewModel(private val favoriteRepository: FavoriteRepository) : BaseVM() {

    private val searchQuery = MutableStateFlow<String?>(null)

    val content = combine(
        favoriteRepository.observeFavorite(),
        searchQuery.debounce(500).distinctUntilChanged().onStart { emit("") }
    ) { favorites, query -> filterContent(favorites, query) }
        .mapList { mapFavoriteToFeedItem(it) }
        .ifEmpty { listOf(EmptyItem(message = "No favorite manga found")) }
        .onStart { emit(listOf(LoadingItem)) }
        .asLiveData(viewModelScope.coroutineContext + Dispatchers.Default)

    private fun filterContent(favorites: List<Favorite>, query: String?): List<Favorite> {
        if (query.isNullOrBlank())
            return favorites
        return favorites.filter { it.manga.title.contains(query, true) }
    }

    private fun mapFavoriteToFeedItem(favorite: Favorite): MangaItem {
        return MangaItem(favorite.manga, MangaItem.MANGA_GRID_ITEM, favorite.newChapterCount)
    }

    fun filterFavorite(query: String?) {
        searchQuery.value = query
    }
}