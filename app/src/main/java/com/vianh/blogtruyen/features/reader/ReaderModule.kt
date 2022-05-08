package com.vianh.blogtruyen.features.reader

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val readerModule
    get() = module {
        viewModel { ReaderViewModel(get(), get(), get()) }
    }