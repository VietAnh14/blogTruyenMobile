package com.vianh.blogtruyen.di

import com.vianh.blogtruyen.features.feed.NewFeedViewModel
import com.vianh.blogtruyen.features.main.MainViewModel
import com.vianh.blogtruyen.features.list.HomeViewModel
import com.vianh.blogtruyen.features.reader.ReaderViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule
    get() = module {
        viewModel { MainViewModel(get(), get()) }
        viewModel { HomeViewModel(get(), get()) }
        viewModel { parameters -> ReaderViewModel(get(), get(), parameters[0], parameters[1], parameters[2]) }
        viewModel { NewFeedViewModel(get(), get()) }
    }