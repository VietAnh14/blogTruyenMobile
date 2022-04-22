package com.vianh.blogtruyen.features.main

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainModule
    get() = module {
        viewModel { MainViewModel(get(), get()) }
    }