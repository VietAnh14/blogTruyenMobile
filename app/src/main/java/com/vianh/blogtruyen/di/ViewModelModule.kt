package com.vianh.blogtruyen.di

import com.vianh.blogtruyen.features.feed.NewFeedViewModel
import com.vianh.blogtruyen.features.home.HomeActivityViewModel
import com.vianh.blogtruyen.features.home.HomeViewModel
import com.vianh.blogtruyen.features.reader.ReaderViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule
    get() = module {
        viewModel { HomeActivityViewModel() }
        viewModel { HomeViewModel(get()) }
        viewModel { parameters -> ReaderViewModel(get(), get(), parameters[0], parameters[1], parameters[2]) }
        viewModel { NewFeedViewModel(get(), get()) }
    }