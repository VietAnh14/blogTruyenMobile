package com.vianh.blogtruyen.features.main

import androidx.lifecycle.viewModelScope
import com.vianh.blogtruyen.features.base.BaseVM
import com.vianh.blogtruyen.features.favorites.data.FavoriteRepository
import com.vianh.blogtruyen.utils.asLiveDataDistinct
import kotlinx.coroutines.Dispatchers

class MainViewModel(private val favoriteRepository: FavoriteRepository): BaseVM() {

    val notificationCount = favoriteRepository
        .getTotalNotificationCount()
        .asLiveDataDistinct(viewModelScope.coroutineContext + Dispatchers.Default)
}