package com.vianh.blogtruyen.features.details

import com.vianh.blogtruyen.features.details.data.AppMangaRepo
import com.vianh.blogtruyen.features.details.data.AppMangaRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val infoModule
    get() = module {
        single<AppMangaRepo> { AppMangaRepository(get()) }
        viewModel { parameters ->
            MangaDetailsViewModel(
                mangaProviderRepo = get(),
                appMangaRepo = get(),
                favoriteRepo = get(),
                localSourceRepo = get(),
                manga = parameters[0],
                isOffline = parameters[1]
            )
        }
    }