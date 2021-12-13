package com.vianh.blogtruyen.features.list

import com.vianh.blogtruyen.features.list.data.CategoryRepo
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val homeModule
    get() = module {
        single { CategoryRepo(get()) }
        viewModel { HomeViewModel(get(), get(), get()) }
    }