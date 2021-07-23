package com.vianh.blogtruyen.features.favorites

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.vianh.blogtruyen.data.model.Favorite
import com.vianh.blogtruyen.features.base.BaseVM
import com.vianh.blogtruyen.features.favorites.data.FavoriteRepository
import com.vianh.blogtruyen.features.home.list.MangaItem
import com.vianh.blogtruyen.utils.mapList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map

class FavoriteViewModel(private val favoriteRepository: FavoriteRepository): BaseVM() {

    val content = favoriteRepository
        .observeFavorite()
        .map { favorites -> favorites.sortedByDescending { it.subscribeTime } }
        .mapList { mapFavoriteToFeedItem(it) }
        .asLiveData(viewModelScope.coroutineContext + Dispatchers.Default)

    private fun mapFavoriteToFeedItem(favorite: Favorite): MangaItem {
        return MangaItem(favorite.manga, favorite.newChapterCount)
    }
}