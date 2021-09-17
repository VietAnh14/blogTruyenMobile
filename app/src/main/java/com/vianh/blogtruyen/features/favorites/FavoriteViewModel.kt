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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEmpty

class FavoriteViewModel(private val favoriteRepository: FavoriteRepository): BaseVM() {

    val content = favoriteRepository
        .observeFavorite()
        .map { favorites -> favorites.sortedByDescending { it.subscribeTime } }
        .mapList { mapFavoriteToFeedItem(it) }
        .ifEmpty { listOf(EmptyItem(message = "No favorite manga found")) }
        .asLiveData(viewModelScope.coroutineContext + Dispatchers.Default)

    private fun mapFavoriteToFeedItem(favorite: Favorite): MangaItem {
        return MangaItem(favorite.manga, favorite.newChapterCount)
    }
}