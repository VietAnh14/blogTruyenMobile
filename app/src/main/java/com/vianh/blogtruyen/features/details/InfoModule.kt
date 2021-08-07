package com.vianh.blogtruyen.features.details

import com.vianh.blogtruyen.features.details.data.MangaRepo
import com.vianh.blogtruyen.features.details.data.MangaRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val infoModule
    get() = module {
        single<MangaRepo> { MangaRepository(get(), get()) }
        viewModel { parameters ->
            MangaDetailsViewModel(
                get(),
                get(),
                get(),
                manga = parameters[0],
                isOffline = parameters[1]
            )
        }
    }