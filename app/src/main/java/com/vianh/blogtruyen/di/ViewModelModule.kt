package com.vianh.blogtruyen.di

import com.vianh.blogtruyen.features.feed.NewFeedViewModel
import com.vianh.blogtruyen.features.main.MainViewModel
import com.vianh.blogtruyen.features.list.HomeViewModel
import com.vianh.blogtruyen.features.reader.ReaderViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule
    get() = module {
        viewModel { MainViewModel(get()) }
        viewModel { HomeViewModel(get(), get()) }
        viewModel { parameters -> ReaderViewModel(parameters[0] ,get(), get()) }
        viewModel { NewFeedViewModel(get(), get()) }
    }