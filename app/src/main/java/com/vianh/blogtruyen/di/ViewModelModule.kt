package com.vianh.blogtruyen.di

import com.vianh.blogtruyen.ui.home.HomeViewModel
import com.vianh.blogtruyen.ui.mangaDetails.MangaDetailsViewModel
import com.vianh.blogtruyen.ui.reader.ReaderViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule
    get() = module {
        viewModel { HomeViewModel(get()) }
        viewModel { parameters -> MangaDetailsViewModel(get(), manga = parameters.get()) }
        viewModel { parameters -> ReaderViewModel(get(), chapter = parameters.get()) }
    }