package com.vianh.blogtruyen.data.prefs

import org.koin.dsl.module

val prefsModule
    get() = module {
        single { AppSettings(get()) }
    }