package com.vianh.blogtruyen.features.details

import com.vianh.blogtruyen.features.details.data.AppMangaRepo
import com.vianh.blogtruyen.features.details.data.AppMangaRepository
import com.vianh.blogtruyen.features.details.ui.MangaDetailsViewModel
import com.vianh.blogtruyen.data.repo.LocalSourceRepo
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val infoModule
    get() = module {
        single<AppMangaRepo> { AppMangaRepository(get()) }
        viewModel { parameters ->
            MangaDetailsViewModel(
                providerRepoManager = get(),
                appMangaRepo = get(),
                favoriteRepo = get(),
                localSourceRepo = get(named(LocalSourceRepo.REPO_NAME)),
                manga = parameters[0],
                isOffline = parameters[1]
            )
        }
    }