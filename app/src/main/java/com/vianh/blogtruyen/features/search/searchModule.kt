package com.vianh.blogtruyen.features.search

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val searchModule
    get() = module {
        viewModel { SearchViewModel(get()) }
    }