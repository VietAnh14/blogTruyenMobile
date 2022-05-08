package com.vianh.blogtruyen.features.local

import com.vianh.blogtruyen.data.repo.LocalSourceRepo
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val localModule
    get() = module {
        viewModel { LocalViewModel(get(named(LocalSourceRepo.REPO_NAME))) }
    }