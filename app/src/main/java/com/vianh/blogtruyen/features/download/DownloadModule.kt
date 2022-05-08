package com.vianh.blogtruyen.features.download

import com.vianh.blogtruyen.data.repo.LocalSourceRepo
import org.koin.core.qualifier.named
import org.koin.dsl.module

val downloadModule
    get() = module {
        factory { DownloadHelper(get(), get(), get(), get(named(LocalSourceRepo.REPO_NAME)), get()) }
    }