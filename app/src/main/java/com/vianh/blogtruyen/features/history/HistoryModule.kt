package com.vianh.blogtruyen.features.history

import com.vianh.blogtruyen.features.history.data.HistoryRepo
import com.vianh.blogtruyen.features.history.data.HistoryRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val historyModule
    get() = module {
        single<HistoryRepository> { HistoryRepo(get()) }
        viewModel { HistoryViewModel(get()) }
    }