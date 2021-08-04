package com.vianh.blogtruyen.features.download

import org.koin.dsl.module

val downloadModule
    get() = module {
        factory { DownloadHelper(get(), get(), get(), get()) }
    }