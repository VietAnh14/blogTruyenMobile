package com.vianh.blogtruyen.features.mangaDetails.mangaInfo

import com.vianh.blogtruyen.features.mangaDetails.MangaDetailsViewModel
import com.vianh.blogtruyen.features.mangaDetails.data.MangaRepo
import com.vianh.blogtruyen.features.mangaDetails.data.MangaRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val infoModule
    get() = module {
        single<MangaRepo> { MangaRepository(get(), get()) }
        viewModel { parameters -> MangaDetailsViewModel(get(), get(), manga = parameters.get()) }
    }