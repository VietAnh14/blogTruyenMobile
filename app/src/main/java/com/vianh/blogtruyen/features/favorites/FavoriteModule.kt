package com.vianh.blogtruyen.features.favorites

import com.vianh.blogtruyen.features.favorites.data.FavoriteRepo
import com.vianh.blogtruyen.features.favorites.data.FavoriteRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val favoriteModule
    get() = module {
        viewModel { FavoriteViewModel() }
        single<FavoriteRepository> { FavoriteRepo(get()) }
    }