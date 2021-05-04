package com.vianh.blogtruyen.di

import com.vianh.blogtruyen.features.home.HomeActivityViewModel
import com.vianh.blogtruyen.features.home.HomeViewModel
import com.vianh.blogtruyen.features.mangaDetails.MangaDetailsViewModel
import com.vianh.blogtruyen.features.reader.ReaderViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule
    get() = module {
        viewModel { HomeActivityViewModel() }
        viewModel { HomeViewModel(get()) }
        viewModel { parameters -> MangaDetailsViewModel(get(), manga = parameters.get()) }
        viewModel { parameters -> ReaderViewModel(get(), chapter = parameters.get(), parameters.get()) }
    }