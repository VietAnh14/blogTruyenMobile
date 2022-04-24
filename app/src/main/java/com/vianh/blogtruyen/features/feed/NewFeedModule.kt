package com.vianh.blogtruyen.features.feed

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val newFeedModule
    get() = module {
        viewModel { NewFeedViewModel(get(), get()) }
    }