package com.vianh.blogtruyen.features.local

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val localModule
    get() = module {
        single { LocalSourceRepo(get(), get()) }
        viewModel { LocalViewModel(get()) }
    }